package com.mediqueue.notification.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Event published when an existing appointment is cancelled.
 * <p>
 * This event is produced by the patient-service and consumed by downstream services
 * such as notification-service to inform patients about cancellation.
 * </p>
 *
 * <p>
 * This DTO serves as a Kafka message payload and should remain backward-compatible
 * to prevent breaking existing consumers.
 * </p>
 */
@Getter
@AllArgsConstructor
public class AppointmentCancelledEvent {

    /**
     * Unique identifier of the appointment that was cancelled.
     * <p>
     * Used for tracking, correlation, and idempotency.
     * </p>
     */
    private String appointmentId;

    /**
     * Unique identifier of the patient.
     */
    private String patientId;

    /**
     * Email address of the patient.
     * <p>
     * Used to send cancellation notification.
     * </p>
     */
    private String patientEmail;

    /**
     * Full name of the patient.
     * <p>
     * Used for personalization in notification messages.
     * </p>
     */
    private String patientName;

    /**
     * Name of the doctor associated with the cancelled appointment.
     */
    private String doctorName;

    /**
     * Date of the scheduled appointment that was cancelled.
     */
    private LocalDate slotDate;

    /**
     * Start time of the appointment that was cancelled.
     */
    private LocalTime startTime;
}
