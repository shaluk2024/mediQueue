package com.mediqueue.patient.exception;

/**
 * Custom exception representing a slot booking conflict (HTTP 409).
 *
 * This exception is thrown when:
 * - A doctor slot is already booked by another patient
 * - Concurrent booking attempts to occur (race condition)
 *
 * Common scenario:
 * - Two users try to book the same slot at the same time
 * - One succeeds, the other fails due to optimistic locking
 *
 * Handled globally by GlobalExceptionHandler to return HTTP 409 (Conflict).
 */
public class SlotAlreadyBookedException extends RuntimeException {

    /**
     * Constructs a new SlotAlreadyBookedException with a specific error message.
     *
     * @param message detailed message explaining why the slot could not be booked
     */
    public SlotAlreadyBookedException(String message) {
        super(message);
    }
}