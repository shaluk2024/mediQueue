package com.mediqueue.patient.data;

/**
 * Enum representing slot status.
 */
public enum SlotStatus {

    /**
     * Slot is available for booking.
     */
    AVAILABLE,

    /**
     * Slot has been booked by a patient.
     */
    BOOKED,

    /**
     * Slot has been cancelled or blocked.
     */
    CANCELLED
}
