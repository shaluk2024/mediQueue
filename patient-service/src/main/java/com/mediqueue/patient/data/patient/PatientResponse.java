package com.mediqueue.patient.data.patient;

import com.mediqueue.patient.entity.Patient;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO representing patient profile details returned to client.
 *
 * Used in:
 * - Patient profile view
 * - Admin patient listing
 */
@Data
@Builder
public class PatientResponse {

    /** Unique internal code of the patient profile. */
    private String patientId;

    /** Internal code of the linked user account. */
    private String userId;

    /** Full name of the patient (from User). */
    private String name;

    /** Email of the patient (from User). */
    private String email;

    /** Phone number of the patient (from User). */
    private String phone;

    /** Date of birth. */
    private LocalDate dateOfBirth;

    /** Gender: MALE, FEMALE, OTHER. */
    private String gender;

    /** Blood group: A+, B+, O+, AB+ etc. */
    private String bloodGroup;

    /** Known medical history, allergies, chronic conditions. */
    private String medicalHistory;

    /**
     * Converts Patient entity → DTO.
     *
     * @param p Patient entity
     * @return PatientResponse DTO
     */
    public static PatientResponse from(final Patient p) {
        return PatientResponse.builder()
                .patientId(p.getInternalCode())
                .userId(p.getUser().getInternalCode())
                .name(p.getUser().getName())
                .email(p.getUser().getEmail())
                .phone(p.getUser().getPhone())
                .dateOfBirth(p.getDateOfBirth())
                .gender(p.getGender())
                .bloodGroup(p.getBloodGroup())
                .medicalHistory(p.getMedicalHistory())
                .build();
    }
}
