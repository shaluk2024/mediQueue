package com.mediqueue.patient.data.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for user login request.
 *
 * Contains credentials required for authentication.
 * Used in:
 * - Login API
 */
@Data
public class LoginRequest {

    /**
     * User's registered email.
     */
    @NotBlank
    @Email
    private String email;

    /**
     * User's password (plain text).
     * Will be verified using PasswordEncoder.
     */
    @NotBlank
    private String password;
}
