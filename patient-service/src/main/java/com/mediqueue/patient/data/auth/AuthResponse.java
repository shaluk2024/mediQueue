package com.mediqueue.patient.data.auth;

import com.mediqueue.patient.data.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for authentication response.
 *
 * Returned after:
 * - Successful login
 * - Successful registration
 * - Token refresh
 *
 * Contains tokens and basic user info.
 */
@Data
@AllArgsConstructor
public class AuthResponse {

    /**
     * JWT access token (short-lived).
     * Used for authenticated API calls.
     */
    private String accessToken;

    /**
     * JWT refresh token (long-lived).
     * Used to generate new access tokens.
     */
    private String refreshToken;

    /**
     * Unique ID of the user.
     */
    private String userId;

    /**
     * Name of the user.
     */
    private String name;

    /**
     * Role of the user (e.g., ADMIN, DOCTOR).
     */
    private Role role;
}
