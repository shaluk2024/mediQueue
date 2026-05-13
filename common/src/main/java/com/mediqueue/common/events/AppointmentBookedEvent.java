package com.mediqueue.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Event published when an appointment is successfully booked.
 *
 * Used by:
 * - Notification service (email/SMS)
 * - Analytics service
 * - Queue management system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentBookedEvent {

    /**
     * Unique ID of the appointment.
     */
    private String appointmentId;

    /**
     * Patient details.
     */
    private String patientId;
    private String patientName;
    private String patientEmail;

    /**
     * Doctor details.
     */
    private String doctorId;
    private String doctorName;

    /**
     * Slot details.
     */
    private LocalDate slotDate;
    private LocalTime startTime;

    /**
     * Symptoms provided by patient.
     */
    private String symptoms;

    /**
     * Type of event (used for routing/processing).
     */
    private EventType eventType = EventType.APPOINTMENT_BOOKED;
}
