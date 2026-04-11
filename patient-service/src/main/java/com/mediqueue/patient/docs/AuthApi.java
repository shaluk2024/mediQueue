package com.mediqueue.patient.docs;

import com.mediqueue.patient.data.auth.AuthResponse;
import com.mediqueue.patient.data.auth.LoginRequest;
import com.mediqueue.patient.data.auth.RefreshRequest;
import com.mediqueue.patient.data.auth.RegisterRequest;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Authentication", description = "User registration, login and token refresh APIs. No authentication required.")
public interface AuthApi {

    // =========================================================
    // REGISTER
    // =========================================================

    @Operation(
            summary = "Register a new user",
            description = "Registers a new user and returns JWT tokens (auto-login)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully",
                    content = @Content(
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                      "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                      "userId": "USR-101-201",
                                      "name": "Rahul Verma",
                                      "role": "PATIENT"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    ResponseEntity<AuthResponse> register(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Registration request payload",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RegisterRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "email": "rahul@gmail.com",
                                      "password": "password123",
                                      "name": "Rahul Verma",
                                      "phone": "9876543210",
                                      "role": "PATIENT"
                                    }
                                    """
                            )
                    )
            )
            @RequestBody RegisterRequest request
    );

    // =========================================================
    // LOGIN
    // =========================================================

    @Operation(
            summary = "Login with email and password",
            description = "Authenticates user and returns access & refresh tokens."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                      "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                      "userId": "USR-101-201",
                                      "name": "Rahul Verma",
                                      "role": "PATIENT"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    ResponseEntity<AuthResponse> login(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Login request payload",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "email": "rahul@gmail.com",
                                      "password": "password123"
                                    }
                                    """
                            )
                    )
            )
            @RequestBody LoginRequest request
    );

    // =========================================================
    // REFRESH TOKEN
    // =========================================================

    @Operation(
            summary = "Refresh access token",
            description = "Generates new access & refresh tokens using a valid refresh token."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Token refreshed successfully",
                    content = @Content(
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "accessToken": "new-access-token...",
                                      "refreshToken": "new-refresh-token...",
                                      "userId": "USR-101-201",
                                      "name": "Rahul Verma",
                                      "role": "PATIENT"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    ResponseEntity<AuthResponse> refresh(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Refresh token request",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RefreshRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                                    }
                                    """
                            )
                    )
            )
            @RequestBody RefreshRequest request
    );
}