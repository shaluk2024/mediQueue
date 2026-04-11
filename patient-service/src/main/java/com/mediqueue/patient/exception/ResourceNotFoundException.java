package com.mediqueue.patient.exception;

/**
 * Custom exception representing a resource not found error (HTTP 404).
 *
 * This exception is thrown when:
 * - Requested entity does not exist in the database
 * - Invalid ID is provided
 * - Resource has been deleted or is unavailable
 *
 * Examples:
 * - Patient not found
 * - Doctor not found
 * - Appointment not found
 *
 * Handled globally by GlobalExceptionHandler to return HTTP 404 response.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with a specific error message.
     *
     * @param message detailed message describing which resource was not found
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}