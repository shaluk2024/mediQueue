package com.mediqueue.patient.service;

import com.mediqueue.patient.data.Role;
import com.mediqueue.patient.data.auth.AuthResponse;
import com.mediqueue.patient.data.auth.LoginRequest;
import com.mediqueue.patient.data.auth.RefreshRequest;
import com.mediqueue.patient.data.auth.RegisterRequest;
import com.mediqueue.patient.entity.User;
import com.mediqueue.patient.exception.BadRequestException;
import com.mediqueue.patient.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service responsible for handling authentication-related operations.
 *
 * Provides:
 * - User registration
 * - User login
 * - Token refresh
 *
 * Integrates with:
 * - UserRepository (database operations)
 * - PasswordEncoder (password hashing/verification)
 * - JwtService (token generation and validation)
 */
@Service // Marks this as a Spring service
@RequiredArgsConstructor // Lombok: constructor injection for final fields
public class AuthService {

    /**
     * Repository for performing CRUD operations on User entity.
     */
    private final UserRepository userRepository;

    /**
     * Password encoder (BCrypt) used for hashing and verifying passwords.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Service for generating and validating JWT tokens.
     */
    private final JwtService jwtService;

    /**
     * Register a new user.
     *
     * Steps:
     * 1. Check if email already exists
     * 2. Determine role (default = PATIENT)
     * 3. Encode password
     * 4. Save user in DB
     * 5. Generate JWT tokens
     *
     * @param request Registration request DTO
     * @return AuthResponse containing tokens and user details
     */
    public AuthResponse register(final RegisterRequest request) {

        // Check for duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        /**
         * Determine user role:
         * - If provided → use it
         * - Else → default to PATIENT
         */
        final Role role = request.getRole() != null
                ? request.getRole()
                : Role.PATIENT;

        // Build User entity
         User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Encrypt password
                .name(request.getName())
                .phone(request.getPhone())
                .role(role)
                .build();

        // Save user to database
        user = userRepository.save(user);

        // Generate tokens and response
        return buildAuthResponse(user);
    }

    /**
     * Authenticate user (login).
     *
     * Steps:
     * 1. Fetch user by email
     * 2. Validate password
     * 3. Generate JWT tokens
     *
     * @param request Login request DTO
     * @return AuthResponse containing tokens and user details
     */
    public AuthResponse login(final LoginRequest request) {

        // Fetch user by email
        final User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        // Generate tokens and return response
        return buildAuthResponse(user);
    }

    /**
     * Refresh access token using refresh token.
     *
     * Steps:
     * 1. Validate refresh token
     * 2. Extract userId from token
     * 3. Fetch user from DB
     * 4. Generate new tokens
     *
     * @param request Refresh request DTO
     * @return New AuthResponse with fresh tokens
     */
    public AuthResponse refresh(RefreshRequest request) {

        // Validate refresh token
        if (!jwtService.isTokenValid(request.getRefreshToken())) {
            throw new BadRequestException("Invalid or expired refresh token");
        }

        // Extract user ID from token
        final String userId = jwtService.extractUserId(request.getRefreshToken());

        // Fetch user from DB
        final User user = userRepository.findByInternalCode(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        // Generate new tokens
        return buildAuthResponse(user);
    }

    /**
     * Helper method to build authentication response.
     *
     * Generates:
     * - Access token (short-lived)
     * - Refresh token (long-lived)
     *
     * @param user Authenticated user
     * @return AuthResponse DTO
     */
    private AuthResponse buildAuthResponse(final User user) {

        // Generate access token with claims
        final String accessToken  = jwtService.generateAccessToken(
                user.getInternalCode(),
                user.getEmail(),
                String.valueOf(user.getRole())
        );

        // Generate refresh token
        final String refreshToken = jwtService.generateRefreshToken(user.getInternalCode());

        // Return response DTO
        return new AuthResponse(
                accessToken,
                refreshToken,
                user.getInternalCode(),
                user.getName(),
                user.getRole()
        );
    }
}
