package com.mediqueue.patient.data;

/**
 * Enum representing user roles in the system.
 *
 * These roles are used for:
 * - Authorization (Spring Security)
 * - Access control (RBAC)
 * - Defining permissions for different types of users
 *
 * Stored as STRING in the database when used with @Enumerated(EnumType.STRING),
 * ensuring readability and stability (avoids ordinal issues).
 */
public enum Role {

    /**
     * Represents a patient in the system.
     * Can book appointments, view records, etc.
     */
    PATIENT,

    /**
     * Represents a doctor.
     * Can manage appointments, view patient data, update queue, etc.
     */
    DOCTOR,

    /**
     * Represents an administrator.
     * Has elevated privileges such as managing users,
     * system configuration, and monitoring.
     */
    ADMIN;

    /**
     * Converts role into Spring Security authority format.
     *
     * Example:
     * ADMIN → ROLE_ADMIN
     *
     * This is required because Spring Security expects roles
     * to be prefixed with "ROLE_".
     */
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
