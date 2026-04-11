package com.mediqueue.patient.data.appointment;

import com.mediqueue.patient.entity.DoctorSlot;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO representing a doctor's available slot.
 *
 * Used in:
 * - Slot listing API
 * - Appointment booking UI
 */
@Data
@Builder
public class SlotResponse {

    /**
     * Unique ID of the slot.
     */
    private String id;

    /**
     * Date of the slot.
     */
    private LocalDate slotDate;

    /**
     * Slot start time.
     */
    private LocalTime startTime;

    /**
     * Slot end time.
     */
    private LocalTime endTime;

    /**
     * Current status of slot (AVAILABLE, BOOKED, etc.).
     */
    private String status;

    /**
     * Converts DoctorSlot entity → DTO.
     *
     * @param s DoctorSlot entity
     * @return SlotResponse DTO
     */
    public static SlotResponse from(DoctorSlot s) {
        return SlotResponse.builder()
                .id(s.getInternalCode())
                .slotDate(s.getSlotDate())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .status(s.getStatus().name())
                .build();
    }
}
