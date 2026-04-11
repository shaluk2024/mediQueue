package com.mediqueue.patient.data.appointment;

import com.mediqueue.patient.entity.Appointment;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO representing appointment details returned to client.
 *
 * Used in:
 * - Appointment history
 * - Booking confirmation
 * - Doctor dashboard
 */
@Data
@Builder
public class AppointmentResponse {

    /**
     * Unique ID of the appointment.
     */
    private String appointmentId;

    /**
     * Name of the patient.
     */
    private String patientName;

    /**
     * Name of the patient.
     */
    private String doctorId;

    /**
     * Name of the doctor.
     */
    private String doctorName;

    /**
     * Doctor's specialization.
     */
    private String specialization;

    /**
     * Date of appointment slot.
     */
    private LocalDate slotDate;

    /**
     * Start time of appointment.
     */
    private LocalTime startTime;

    /**
     * End time of appointment.
     */
    private LocalTime endTime;

    /**
     * Current status of appointment (CONFIRMED, CANCELLED, etc.).
     */
    private String status;

    /**
     * AI-based triage priority (LOW, MEDIUM, HIGH, CRITICAL).
     */
    private String triagePriority;

    /**
     * Explanation provided by AI for triage decision.
     */
    private String triageReasoning;

    /**
     * Position of patient in queue.
     */
    private Integer queuePosition;

    /**
     * Converts Appointment entity → DTO.
     *
     * @param a Appointment entity
     * @return AppointmentResponse DTO
     */
    public static AppointmentResponse from(Appointment a) {
        return AppointmentResponse.builder()
                .appointmentId(a.getInternalCode())
                .patientName(a.getPatient().getName())
                .doctorId(a.getDoctor().getInternalCode())
                .doctorName(a.getDoctor().getUser().getName())
                .specialization(a.getDoctor().getSpecialization())
                .slotDate(a.getSlot().getSlotDate())
                .startTime(a.getSlot().getStartTime())
                .endTime(a.getSlot().getEndTime())
                .status(a.getStatus().name())
                .triagePriority(a.getTriagePriority() != null ? a.getTriagePriority().name() : null)
                .triageReasoning(a.getTriageReasoning())
                .queuePosition(a.getQueuePosition())
                .build();
    }
}
