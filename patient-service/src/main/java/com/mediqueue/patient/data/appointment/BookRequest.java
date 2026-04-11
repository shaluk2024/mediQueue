package com.mediqueue.patient.data.appointment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for booking an appointment.
 *
 * Used by:
 * - Patient to book a slot with a doctor
 */
@Data
public class BookRequest {

    /**
     * ID of the selected doctor slot.
     */
    @NotBlank
    private String slotId;

    /**
     * ID of the doctor for whom appointment is being booked.
     */
    @NotBlank
    private String doctorId;

    /**
     * Symptoms described by the patient.
     * Used for AI triage and doctor's reference.
     */
    private String symptoms;
}
