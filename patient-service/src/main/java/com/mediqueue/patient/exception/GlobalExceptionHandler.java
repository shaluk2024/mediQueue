package com.mediqueue.patient.exception;

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
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
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
    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
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
    public ResponseEntity<Map<String, Object>> handleSlotBooked(SlotAlreadyBookedException ex) {
        return error(HttpStatus.CONFLICT, ex.getMessage());
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
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        return error(HttpStatus.UNAUTHORIZED, ex.getMessage());
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
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        return error(HttpStatus.BAD_REQUEST, message);
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
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {

        log.error("Unexpected error", ex);

        return error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
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
    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {

        Map<String, Object> body = new HashMap<>();

        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);

        return ResponseEntity.status(status).body(body);
    }
}
