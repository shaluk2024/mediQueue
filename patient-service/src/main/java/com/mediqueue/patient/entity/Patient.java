package com.mediqueue.patient.entity;

import com.mediqueue.patient.util.CodeUtil;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Entity representing a patient.
 */
@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient extends BaseEntity{

    /**
     * Primary key of patient.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "patients_seq_gen")
    @SequenceGenerator(
            name = "patients_seq_gen",
            sequenceName = "patients_id_seq",
            allocationSize = 1
    )
    private Long id;

    /**
     * One-to-one mapping with User entity.
     *
     * Each patient is also a user in the system.
     * This links patient-specific data with authentication/user data.
     *
     * Foreign key: user_id
     */
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Date of birth of patient.
     */
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    /**
     * Gender of patient.
     * Example values: MALE, FEMALE, OTHER
     */
    @Column(length = 10)
    private String gender;

    /**
     * Blood group of patient.
     * Example: A+, O-, AB+
     */
    @Column(name = "blood_group", length = 5)
    private String bloodGroup;

    /**
     * Medical history notes.
     * Can include allergies, chronic diseases, etc.
     */
    @Column(name = "medical_history", columnDefinition = "TEXT")
    private String medicalHistory;

    /**
     * Generates internalCode before saving if not already set.
     */
    @PrePersist
    public void ensureInternalCode() {
        if (getInternalCode() == null) {
            setInternalCode(CodeUtil.generatePatientCode());
        }
    }
}
