package com.mediqueue.notification.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Event published when an appointment is successfully booked in the system.
 * <p>
 * This event is produced by the patient-service and consumed by downstream services
 * such as notification-service to trigger actions like sending confirmation emails.
 * </p>
 *
 * <p>
 * This DTO represents a Kafka message payload and should remain backward-compatible
 * to avoid breaking existing consumers.
 * </p>
 */
@Getter
@AllArgsConstructor
public class AppointmentBookedEvent {

    /**
     * Unique identifier of the appointment.
     * <p>
     * Used for tracking, correlation, and idempotency checks across services.
     * </p>
     */
    private String appointmentId;

    /**
     * Unique identifier of the patient.
     */
    private String patientId;

    /**
     * Full name of the patient.
     * <p>
     * Used for personalization in notification messages.
     * </p>
     */
    private String patientName;

    /**
     * Email address of the patient.
     * <p>
     * Target address for sending appointment-related notifications.
     * </p>
     */
    private String patientEmail;

    /**
     * Name of the doctor assigned to the appointment.
     */
    private String doctorName;

    /**
     * Date of the scheduled appointment.
     */
    private LocalDate slotDate;

    /**
     * Start time of the appointment.
     */
    private LocalTime startTime;

    /**
     * Symptoms provided by the patient at the time of booking.
     * <p>
     * Optional field used for context or triage purposes.
     * </p>
     */
    private String symptoms;
}
