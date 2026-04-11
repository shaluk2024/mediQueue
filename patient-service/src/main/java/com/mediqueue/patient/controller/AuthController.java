package com.mediqueue.patient.controller;

import com.mediqueue.patient.data.auth.AuthResponse;
import com.mediqueue.patient.data.auth.LoginRequest;
import com.mediqueue.patient.data.auth.RefreshRequest;
import com.mediqueue.patient.data.auth.RegisterRequest;
import com.mediqueue.patient.docs.AuthApi;
import com.mediqueue.patient.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller responsible for authentication operations.
 *
 * Exposes APIs for:
 * - User registration
 * - User login
 * - Token refresh
 *
 * Base path: /api/patients/auth
 */
@RestController // Marks this class as REST controller (returns JSON responses)
@RequestMapping("/api/patients/auth") // Base URL for all endpoints
@RequiredArgsConstructor // Lombok: constructor injection
public class AuthController implements AuthApi {

    /**
     * Service layer handling authentication logic.
     */
    private final AuthService authService;

    /**
     * Register a new user.
     *
     * Endpoint: POST /api/patients/auth/register
     *
     * Validates request body using @Valid.
     * Delegates registration logic to AuthService.
     * Here, we are giving auto-login facility just after sign-up.
     *
     * @param request registration request DTO
     * @return AuthResponse containing tokens and user info
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody final RegisterRequest request) {
        // Logic is delegated to the service
        final var response = authService.register(request);

        // Return 201 Created instead of 200 OK
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authenticate user (login).
     *
     * Endpoint: POST /api/patients/auth/login
     *
     * Validates credentials and returns JWT tokens.
     *
     * @param request login request DTO
     * @return AuthResponse containing access & refresh tokens
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody final LoginRequest request) {
        // Delegate authentication logic to the service layer
        final var response = authService.login(request);

        // Return 200 OK for successful login
        return ResponseEntity.ok(response);
    }

    /**
     * Refresh access token using refresh token.
     *
     * Endpoint: POST /api/patients/auth/refresh
     *
     * Validates refresh token and generates new access token.
     *
     * @param request refresh request DTO
     * @return AuthResponse with new tokens
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody final RefreshRequest request) {
        // Logic is delegated to the service
        final var response = authService.refresh(request);

        // Return 200 OK for successful token generation
        return ResponseEntity.ok(response);
    }
}
