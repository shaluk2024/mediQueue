package com.mediqueue.patient.data.events;

/**
 * Enum representing different types of events in the system.
 *
 * Used in:
 * - Kafka event payloads
 * - Event-driven communication between microservices
 * - Routing and processing logic in consumers
 *
 * Benefits:
 * - Type safety (avoids string typos)
 * - Centralized definition of all event types
 * - Easy to extend for future events
 */
public enum EventType {

    /**
     * Triggered when an appointment is successfully booked.
     *
     * Used by:
     * - Notification service (email/SMS confirmation)
     * - Queue service (add patient to queue)
     * - Analytics service
     */
    APPOINTMENT_BOOKED,

    /**
     * Triggered when an appointment is cancelled.
     *
     * Used by:
     * - Notification service (cancellation alert)
     * - Slot service (mark slot as available again)
     */
    APPOINTMENT_CANCELLED,

    /**
     * Triggered when a patient's queue position is updated.
     *
     * Used by:
     * - Real-time UI updates
     * - Notification service (position change alerts)
     */
    QUEUE_UPDATED;

    /**
     * Returns event type as string.
     *
     * Useful when:
     * - Sending event over Kafka
     * - Logging
     *
     * @return event type name
     */
    public String getValue() {
        return this.name();
    }
}
