package com.mediqueue.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event published when queue position is updated.
 *
 * Used by:
 * - Real-time UI updates
 * - Notification systems
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueUpdatedEvent {

    /**
     * Appointment identifier.
     */
    private String appointmentId;

    /**
     * Patient details.
     */
    private String patientId;
    private String patientEmail;

    /**
     * Updated queue position.
     */
    private Integer newQueuePosition;

    /**
     * Triage priority affecting queue.
     */
    private String triagePriority;

    /**
     * Event type identifier.
     */
    private String eventType = "QUEUE_UPDATED";
}
