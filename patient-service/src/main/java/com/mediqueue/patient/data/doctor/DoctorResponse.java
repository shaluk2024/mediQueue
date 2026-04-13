package com.mediqueue.patient.data.doctor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mediqueue.patient.entity.Doctor;
import lombok.Builder;
import lombok.Data;

/**
 * DTO representing doctor profile details returned to client.
 *
 * Used in:
 * - Doctor search results
 * - Doctor profile view
 * - Appointment booking flow
 */
@Data
@Builder
public class DoctorResponse {

    /** Unique internal code of the doctor profile. */
    private String doctorId;

    /** Full name of the doctor (from User). */
    private String name;

    /** Email of the doctor (from User). */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email;

    /** Phone number of the doctor (from User). */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String phone;

    /** Medical specialization (e.g. Cardiology, Neurology). */
    private String specialization;

    /** Qualification (e.g. MBBS, MD, DM). */
    private String qualification;

    /** Total years of professional experience. */
    private Integer experienceYears;

    /** Consultation fee charged per visit. */
    private Double consultationFee;

    /** Whether the doctor is currently available for appointments. */
    private boolean available;

    /**
     * Converts Doctor entity → DTO.
     *
     * @param d Doctor entity
     * @return DoctorResponse DTO
     */
    public static DoctorResponse from(final Doctor d) {
        return DoctorResponse.builder()
                .doctorId(d.getInternalCode())
                .name(d.getUser().getName())
                .email(d.getUser().getEmail())
                .phone(d.getUser().getPhone())
                .specialization(d.getSpecialization())
                .qualification(d.getQualification())
                .experienceYears(d.getExperienceYears())
                .consultationFee(d.getConsultationFee())
                .available(d.isAvailable())
                .build();
    }

    public static DoctorResponse map(final Doctor d) {
        return DoctorResponse.builder()
                .doctorId(d.getInternalCode())
                .phone(d.getUser().getPhone())
                .specialization(d.getSpecialization())
                .qualification(d.getQualification())
                .experienceYears(d.getExperienceYears())
                .consultationFee(d.getConsultationFee())
                .available(d.isAvailable())
                .build();
    }


}