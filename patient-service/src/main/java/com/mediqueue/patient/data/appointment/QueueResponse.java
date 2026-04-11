package com.mediqueue.patient.data.appointment;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

/**
 * DTO representing doctor's queue view.
 *
 * Used in:
 * - Doctor dashboard
 * - Queue management system
 */
@Data
@Builder
public class QueueResponse {

    /**
     * Appointment ID.
     */
    private String appointmentId;

    /**
     * Patient's name.
     */
    private String patientName;

    /**
     * AI triage priority.
     */
    private String triagePriority;

    /**
     * Position in queue.
     */
    private Integer queuePosition;

    /**
     * Slot start time.
     */
    private LocalTime startTime;

    /**
     * Symptoms list (can be parsed from string if needed).
     */
    private List<String> symptoms;
}
