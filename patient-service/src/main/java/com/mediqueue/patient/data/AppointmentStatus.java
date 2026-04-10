package com.mediqueue.patient.data;

/**
 * Enum representing appointment lifecycle states.
 */
public enum AppointmentStatus {

    /**
     * Appointment successfully booked.
     */
    CONFIRMED,

    /**
     * Appointment cancelled.
     */
    CANCELLED,

    /**
     * Appointment completed successfully.
     */
    COMPLETED,

    /**
     * Patient did not attend the appointment.
     */
    NO_SHOW
}
