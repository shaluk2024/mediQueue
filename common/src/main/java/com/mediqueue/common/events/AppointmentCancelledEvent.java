package com.mediqueue.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Event published when an appointment is canceled.
 *
 * Used by:
 * - Notification service
 * - Slot management (free slot)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentCancelledEvent {

    /**
     * Appointment identifier.
     */
    private String appointmentId;

    /**
     * Patient details.
     */
    private String patientId;
    private String patientEmail;
    private String patientName;

    /**
     * Doctor details.
     */
    private String doctorName;

    /**
     * Slot details.
     */
    private LocalDate slotDate;
    private LocalTime startTime;

    /**
     * Event type identifier.
     */
    private EventType eventType = EventType.APPOINTMENT_CANCELLED;
}
