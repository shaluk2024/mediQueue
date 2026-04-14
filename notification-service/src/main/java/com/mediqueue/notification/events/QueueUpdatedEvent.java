package com.mediqueue.notification.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Event published when the queue position of an appointment is updated.
 * <p>
 * This event is typically produced by the queue-management component and
 * consumed by downstream services such as notification-service to inform
 * patients about their updated position in the queue.
 * </p>
 *
 * <p>
 * This DTO represents a Kafka message payload and should remain backward-compatible
 * to avoid breaking existing consumers.
 * </p>
 */
@Getter
@AllArgsConstructor
public class QueueUpdatedEvent {

    /**
     * Unique identifier of the appointment.
     * <p>
     * Used for correlation and idempotency across services.
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
     * Used to send queue update notifications.
     * </p>
     */
    private String patientEmail;

    /**
     * Updated position of the appointment in the queue.
     * <p>
     * Lower values indicate higher priority (e.g., position 1 = next in line).
     * </p>
     */
    private Integer newQueuePosition;

    /**
     * Triage priority associated with the appointment.
     * <p>
     * Example values: HIGH, MEDIUM, LOW.
     * Used to explain queue ordering and urgency to downstream systems.
     * </p>
     */
    private String triagePriority;
}
