package com.mediqueue.patient.data.appointment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for creating a new doctor slot.
 *
 * Used by:
 * - Admin to create available slots for a doctor
 */
@Data
public class CreateSlotRequest {

    /** Internal code of the doctor for whom slot is being created. */
    @NotBlank
    private String doctorId;

    /** Date of the slot. */
    @NotNull
    private LocalDate slotDate;

    /** Start time of the slot. */
    @NotNull
    private LocalTime startTime;

    /** End time of the slot. */
    @NotNull
    private LocalTime endTime;
}