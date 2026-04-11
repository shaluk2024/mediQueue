package com.mediqueue.patient.service;

import com.mediqueue.patient.data.AppointmentStatus;
import com.mediqueue.patient.data.SlotStatus;
import com.mediqueue.patient.data.TriagePriority;
import com.mediqueue.patient.data.appointment.AppointmentResponse;
import com.mediqueue.patient.data.appointment.BookRequest;
import com.mediqueue.patient.data.appointment.SlotResponse;
import com.mediqueue.patient.data.events.AppointmentBookedEvent;
import com.mediqueue.patient.data.events.AppointmentCancelledEvent;
import com.mediqueue.patient.data.events.QueueUpdatedEvent;
import com.mediqueue.patient.entity.Appointment;
import com.mediqueue.patient.entity.Doctor;
import com.mediqueue.patient.entity.DoctorSlot;
import com.mediqueue.patient.entity.User;
import com.mediqueue.patient.exception.BadRequestException;
import com.mediqueue.patient.exception.ResourceNotFoundException;
import com.mediqueue.patient.exception.SlotAlreadyBookedException;
import com.mediqueue.patient.repository.AppointmentRepository;
import com.mediqueue.patient.repository.DoctorRepository;
import com.mediqueue.patient.repository.DoctorSlotRepository;
import com.mediqueue.patient.repository.UserRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service responsible for managing appointment lifecycle.
 *
 * Responsibilities:
 * - Fetch available slots (with caching)
 * - Book appointment (with optimistic locking)
 * - Cancel appointment
 * - Fetch patient appointments
 * - Fetch doctor queue
 * - Update triage priority (AI integration)
 *
 * Integrations:
 * - Kafka (event publishing)
 * - Database (JPA repositories)
 * - Cache (slot caching)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

    /**
     * Repository for Appointment entity.
     */
    private final AppointmentRepository appointmentRepository;

    /**
     * Repository for DoctorSlot entity.
     */
    private final DoctorSlotRepository slotRepository;

    /**
     * Repository for Doctor entity.
     */
    private final DoctorRepository doctorRepository;

    /**
     * Repository for User entity (patient).
     */
    private final UserRepository userRepository;

    /**
     * Kafka template for publishing events.
     */
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Kafka topic for appointment booked event.
     */
    @Value("${kafka.topics.appointment-booked}")
    private String appointmentBookedTopic;

    /**
     * Kafka topic for appointment cancelled event.
     */
    @Value("${kafka.topics.appointment-cancelled}")
    private String appointmentCancelledTopic;

    /**
     * Kafka topic for queue updated event.
     */
    @Value("${kafka.topics.queue-updated}")
    private String queueUpdatedTopic;

    /**
     * Fetch available slots for a doctor on a given date.
     *
     * Uses caching to improve performance.
     * Cache is evicted when a booking happens.
     *
     * @param doctorId doctor ID
     * @param date     slot date
     * @return list of available slots
     */
    @Cacheable(value = "doctorSlots", key = "#doctorId + '_' + #date")
    public List<SlotResponse> getAvailableSlots(final String doctorId, final LocalDate date) {
        return slotRepository.findAvailableSlots(doctorId, date)
                .stream()
                .map(SlotResponse::from)
                .toList();
    }

    /**
     * Book an appointment.
     *
     * Flow:
     * 1. Validate patient & doctor
     * 2. Fetch slot with optimistic locking
     * 3. Mark slot as BOOKED
     * 4. Create appointment
     * 5. Assign queue position
     * 6. Publish Kafka event
     *
     * Handles concurrency using optimistic locking to prevent double booking.
     */
    @Transactional
    @CacheEvict(value = "doctorSlots", key = "#request.doctorId + '_' + #slotDate")
    public AppointmentResponse bookAppointment(
            final BookRequest request, final String patientId, final LocalDate slotDate) {

        // Fetch patient
        final User patient = userRepository.findByInternalCode(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        // Fetch doctor
        final Doctor doctor = doctorRepository.findByInternalCode(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        /**
         * Optimistic locking block:
         * Ensures only one user can book a slot.
         */
        DoctorSlot slot;
        try {
            slot = slotRepository.findAvailableSlotForBooking(request.getSlotId())
                    .orElseThrow(() -> new SlotAlreadyBookedException("Slot is no longer available"));

            slot.setStatus(SlotStatus.BOOKED);
            slotRepository.save(slot);

        } catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
            log.warn("Concurrent booking detected for slot {}", request.getSlotId());
            throw new SlotAlreadyBookedException("Slot already booked by another user");
        }

        // Calculate queue position
        long queuePos = appointmentRepository.countConfirmedForDoctorOnDate(
                doctor.getId(), slotDate) + 1;

        // Create appointment
        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .slot(slot)
                .symptoms(request.getSymptoms())
                .status(AppointmentStatus.CONFIRMED)
                .triagePriority(TriagePriority.MEDIUM)
                .queuePosition((int) queuePos)
                .build();

        appointment = appointmentRepository.save(appointment);

        /**
         * Publish Kafka event for:
         * - Notification service
         * - AI triage service
         */
        AppointmentBookedEvent event = AppointmentBookedEvent.builder()
                .appointmentId(appointment.getInternalCode())
                .patientId(patient.getInternalCode())
                .patientName(patient.getName())
                .patientEmail(patient.getEmail())
                .doctorId(doctor.getInternalCode())
                .doctorName(doctor.getUser().getName())
                .slotDate(slot.getSlotDate())
                .startTime(slot.getStartTime())
                .symptoms(request.getSymptoms())
                .build();

        //kafkaTemplate.send(appointmentBookedTopic, appointment.getInternalCode(), event);

        log.info("Appointment booked: {}", appointment.getId());

        return AppointmentResponse.from(appointment);
    }

    /**
     * Cancel an appointment.
     *
     * Steps:
     * - Validate ownership
     * - Validate status
     * - Update appointment + slot
     * - Publish Kafka event
     */
    @Transactional
    public void cancelAppointment(final String appointmentCode, final String patientCode) {

        Appointment appointment = appointmentRepository.findByInternalCode(appointmentCode)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        // Ensure patient owns appointment
        if (!appointment.getPatient().getInternalCode().equals(patientCode)) {
            throw new BadRequestException("You can only cancel your own appointments");
        }

        // Ensure valid state
        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new BadRequestException("Only confirmed appointments can be cancelled");
        }

        // Update status
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.getSlot().setStatus(SlotStatus.AVAILABLE);

        appointmentRepository.save(appointment);

        // Publish Kafka event
        AppointmentCancelledEvent event = AppointmentCancelledEvent.builder()
                .appointmentId(appointmentCode)
                .patientId(patientCode)
                .patientEmail(appointment.getPatient().getEmail())
                .patientName(appointment.getPatient().getName())
                .doctorName(appointment.getDoctor().getUser().getName())
                .slotDate(appointment.getSlot().getSlotDate())
                .startTime(appointment.getSlot().getStartTime())
                .build();

        //kafkaTemplate.send(appointmentCancelledTopic, appointmentCode, event);

        log.info("Appointment cancelled: {}", appointmentCode);
    }

    /**
     * Fetch all appointments for a patient.
     */
    public List<AppointmentResponse> getMyAppointments(final String patientCode) {
        return appointmentRepository.findAllByPatientCode(patientCode)
                .stream()
                .map(AppointmentResponse::from)
                .toList();
    }

    /**
     * Fetch doctor's queue for a specific date.
     *
     * Only accessible by users with DOCTOR role.
     */
    @PreAuthorize("hasRole('DOCTOR')")
    public List<AppointmentResponse> getDoctorQueue(final String doctorCode, final LocalDate date) {
        return appointmentRepository.findDoctorQueueForDate(doctorCode, date)
                .stream()
                .map(AppointmentResponse::from)
                .toList();
    }

    /**
     * Update triage priority after AI processing.
     *
     * Called by Kafka consumer (Triage Service).
     *
     * Also publishes queue update event.
     */
    @Transactional
    public void updateTriagePriority(final String appointmentCode,
                                     final TriagePriority priority,
                                     final String reasoning) {

        appointmentRepository.findById(appointmentCode).ifPresent(a -> {

            a.setTriagePriority(priority);
            a.setTriageReasoning(reasoning);

            appointmentRepository.save(a);

            // Publish queue update event
            kafkaTemplate.send(queueUpdatedTopic, appointmentCode,
                    QueueUpdatedEvent.builder()
                            .appointmentId(appointmentCode)
                            .patientId(a.getPatient().getInternalCode())
                            .patientEmail(a.getPatient().getEmail())
                            .newQueuePosition(a.getQueuePosition())
                            .triagePriority(priority.name())
                            .build());
        });
    }
}
