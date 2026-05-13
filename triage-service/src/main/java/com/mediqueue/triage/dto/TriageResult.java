package com.mediqueue.triage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TriageResult {
    private String appointmentId;

    private String priority;        // LOW, MEDIUM, HIGH, CRITICAL

    private String reasoning;

    private Double confidence;

    private boolean aiAvailable;

    public static TriageResult fallback(String appointmentId) {
        return TriageResult.builder()
                .appointmentId(appointmentId)
                .priority("MEDIUM")
                .reasoning("AI triage temporarily unavailable — manual review recommended")
                .confidence(0.0)
                .aiAvailable(false)
                .build();
    }
}
