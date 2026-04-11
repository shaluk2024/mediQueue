package com.mediqueue.patient.data.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for refreshing access token.
 *
 * Used when access token expires.
 */
@Data
public class RefreshRequest {

    /**
     * Refresh token issued during login.
     * Must be valid and not expired.
     */
    @NotBlank
    private String refreshToken;
}