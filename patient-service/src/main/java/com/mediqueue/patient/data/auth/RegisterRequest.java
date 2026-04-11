package com.mediqueue.patient.data.auth;

import com.mediqueue.patient.data.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for user registration request.
 *
 * Contains user input required to create a new account.
 * Used in:
 * - Registration API
 */
@Data
public class RegisterRequest {

    /**
     * User's email address.
     * Must be valid and unique.
     */
    @NotBlank
    @Email
    private String email;

    /**
     * User's password.
     * Will be encrypted before storing in DB.
     */
    @NotBlank
    private String password;

    /**
     * Full name of the user.
     */
    @NotBlank
    private String name;

    /**
     * Optional phone number.
     */
    private String phone;

    /**
     * Role of the user (e.g., PATIENT, DOCTOR, ADMIN).
     * Optional → defaults handled in service layer.
     */
    private Role role;
}
