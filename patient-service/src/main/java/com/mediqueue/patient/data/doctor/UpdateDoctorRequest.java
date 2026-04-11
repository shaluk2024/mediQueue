package com.mediqueue.patient.data.doctor;

import lombok.Data;

/**
 * DTO for updating doctor profile details.
 *
 * Used by:
 * - Doctor to update their own profile
 * - Admin to update any doctor's profile
 *
 * All fields are optional — only non-null fields will be updated.
 */
@Data
public class UpdateDoctorRequest {

    /** Updated specialization. */
    private String specialization;

    /** Updated qualification. */
    private String qualification;

    /** Updated years of experience. */
    private Integer experienceYears;

    /** Updated consultation fee. */
    private Double consultationFee;

    /** Updated availability status. */
    private Boolean available;
}