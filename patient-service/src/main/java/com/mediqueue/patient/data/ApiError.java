package com.mediqueue.patient.data;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ApiError {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;

    // 🔥 new
    private String code;

    // 🔥 field-level validation
    private Map<String, String> validationErrors;
}