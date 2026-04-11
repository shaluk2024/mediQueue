package com.mediqueue.patient.exception;

/**
 * Custom exception representing a bad request (HTTP 400).
 *
 * This exception is thrown when:
 * - Input validation fails
 * - Business rules are violated
 * - Client sends invalid or inconsistent data
 *
 * Handled globally by GlobalExceptionHandler to return HTTP 400 response.
 */
public class BadRequestException extends RuntimeException {

    /**
     * Constructs a new BadRequestException with a specific error message.
     *
     * @param message detailed error message describing the cause
     */
    public BadRequestException(String message) {
        super(message);
    }
}