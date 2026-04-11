package com.mediqueue.patient.exception;

import com.mediqueue.patient.data.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 *
 * This class intercepts exceptions thrown from controllers/services
 * and converts them into standardized HTTP responses.
 *
 * Benefits:
 * - Centralized error handling
 * - Consistent API error response structure
 * - Cleaner controller/service code
 */
@RestControllerAdvice // Applies to all REST controllers globally
@Slf4j // Enables logging
public class GlobalExceptionHandler {

    /**
     * Handles ResourceNotFoundException.
     *
     * Typically thrown when:
     * - Entity is not found in DB
     *
     * Returns HTTP 404.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), "RESOURCE_NOT_FOUND", null);
    }

    /**
     * Handles BadRequestException.
     *
     * Typically thrown for:
     * - Invalid input
     * - Business rule violations
     *
     * Returns HTTP 400.
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), "BAD_REQUEST", null);
    }

    /**
     * Handles SlotAlreadyBookedException.
     *
     * Triggered when:
     * - Concurrent booking occurs
     * - Slot is no longer available
     *
     * Returns HTTP 409 (Conflict).
     */
    @ExceptionHandler(SlotAlreadyBookedException.class)
    public ResponseEntity<ApiError> handleSlotBooked(SlotAlreadyBookedException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), "SLOT_ALREADY_BOOKED", null);
    }

    /**
     * Handles authentication failures.
     *
     * Triggered when:
     * - Invalid email/password
     *
     * Returns HTTP 401 (Unauthorized).
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), "INVALID_CREDENTIALS", null);
    }

    /**
     * Handles validation errors from @Valid annotations.
     *
     * Extracts first validation error message.
     *
     * Example:
     * "email: must not be blank"
     *
     * Returns HTTP 400.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));

        return build(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                "VALIDATION_ERROR",
                errors
        );
    }

    /**
     * Handles all unhandled exceptions.
     *
     * Acts as a fallback for unexpected errors.
     * Logs full exception for debugging.
     *
     * Returns HTTP 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneral(Exception ex) {

        log.error("Unexpected error", ex);

        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                "INTERNAL_SERVER_ERROR",
                null
        );
    }

    /**
     * Helper method to build standardized error response.
     *
     * Response format:
     * {
     *   "timestamp": "...",
     *   "status": 400,
     *   "error": "Bad Request",
     *   "message": "Validation failed"
     * }
     *
     * @param status  HTTP status
     * @param message error message
     * @return ResponseEntity with structured error body
     */
    private ResponseEntity<ApiError> build(
            HttpStatus status,
            String message,
            String code,
            Map<String, String> validationErrors
    ) {

        ApiError body = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .code(code)
                .validationErrors(validationErrors)
                .build();

        return ResponseEntity.status(status).body(body);
    }
}