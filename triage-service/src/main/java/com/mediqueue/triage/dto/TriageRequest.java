package com.mediqueue.triage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TriageRequest {
    private String appointmentId;

    private String patientId;

    private String symptoms;

    private String doctorId;
}
