package com.mediqueue.patient.service;

import com.mediqueue.patient.data.SlotStatus;
import com.mediqueue.patient.data.appointment.SlotResponse;
import com.mediqueue.patient.data.appointment.CreateSlotRequest;
import com.mediqueue.patient.entity.DoctorSlot;
import com.mediqueue.patient.exception.BadRequestException;
import com.mediqueue.patient.exception.ResourceNotFoundException;
import com.mediqueue.patient.repository.DoctorRepository;
import com.mediqueue.patient.repository.DoctorSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service responsible for managing doctor slot operations.
 *
 * Responsibilities:
 * - Create new slots for a doctor (Admin only)
 * - Delete a slot (Admin only)
 * - Fetch slots for a doctor on a given date
 *
 * Access control:
 * - Admin: create and delete slots
 * - Public: view available slots
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorSlotService {

    /** Repository for DoctorSlot entity. */
    private final DoctorSlotRepository slotRepository;

    /** Repository for Doctor entity. */
    private final DoctorRepository doctorRepository;

    /**
     * Create a new slot for a doctor.
     *
     * Only accessible by ADMIN.
     * Validates that end time is after start time.
     *
     * @param request create slot request DTO
     * @return created SlotResponse DTO
     * @throws ResourceNotFoundException if doctor not found
     * @throws BadRequestException if end time is before or equal to start time
     */
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public SlotResponse createSlot(final CreateSlotRequest request) {

        final var doctor = doctorRepository.findByInternalCode(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found with code: " + request.getDoctorId()));

        // Validate time range
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new BadRequestException("End time must be after start time");
        }

        final var slot = DoctorSlot.builder()
                .doctor(doctor)
                .slotDate(request.getSlotDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(SlotStatus.AVAILABLE)
                .build();

        final var saved = slotRepository.save(slot);

        log.info("Slot created for doctor {} on {}", request.getDoctorId(), request.getSlotDate());

        return SlotResponse.from(saved);
    }

    /**
     * Delete a slot by internal code.
     *
     * Only accessible by ADMIN.
     * Only AVAILABLE slots can be deleted — booked slots cannot be removed.
     *
     * @param slotCode slot's internal code (e.g. SLT-101-201)
     * @throws ResourceNotFoundException if slot not found
     * @throws BadRequestException if slot is already booked
     */
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteSlot(final String slotCode) {

        final var slot = slotRepository.findByInternalCode(slotCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Slot not found with code: " + slotCode));

        if (slot.getStatus() == SlotStatus.BOOKED) {
            throw new BadRequestException("Cannot delete a booked slot");
        }

        slotRepository.delete(slot);

        log.info("Slot deleted: {}", slotCode);
    }

    /**
     * Get all slots for a doctor on a specific date.
     *
     * Returns all statuses (AVAILABLE, BOOKED, CANCELLED).
     * Useful for admin slot management view.
     *
     * @param doctorCode doctor's internal code
     * @param date       date to fetch slots for
     * @return List of SlotResponse DTOs
     */
    public List<SlotResponse> getSlotsByDoctorAndDate(final String doctorCode, final LocalDate date) {
        return slotRepository.findAvailableSlots(doctorCode, date)
                .stream()
                .map(SlotResponse::from)
                .toList();
    }
}