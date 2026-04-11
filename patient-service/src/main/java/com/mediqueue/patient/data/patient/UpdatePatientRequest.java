package com.mediqueue.patient.data.patient;

import lombok.Data;

import java.time.LocalDate;

/**
 * DTO for updating patient profile details.
 *
 * Used by:
 * - Patient to update their own profile
 * - Admin to update any patient's profile
 *
 * All fields are optional — only non-null fields will be updated.
 */
@Data
public class UpdatePatientRequest {

    /** Updated date of birth. */
    private LocalDate dateOfBirth;

    /** Updated gender: MALE, FEMALE, OTHER. */
    private String gender;

    /** Updated blood group: A+, B+, O+, AB+ etc. */
    private String bloodGroup;

    /** Updated medical history notes. */
    private String medicalHistory;
}