package com.mediqueue.patient.entity;

import com.mediqueue.patient.util.CodeUtil;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a Doctor in the system.
 */
@Entity // Marks this class as a JPA entity
@Table(name = "doctors") // Maps to "doctors" table in DB
@Getter
@Setter // Lombok: generates getters and setters
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Lombok: no-args constructor
@AllArgsConstructor // Lombok: all-args constructor
@Builder // Lombok: builder pattern support
public class Doctor extends BaseEntity{

    /**
     * Primary key for Doctor entity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "doctors_seq_gen")
    @SequenceGenerator(
            name = "doctors_seq_gen",
            sequenceName = "doctors_id_seq",
            allocationSize = 1
    )
    private Long id;

    /**
     * One-to-one mapping with User entity.
     *
     * Each doctor is also a user in the system.
     * This links doctor-specific data with authentication/user data.
     *
     * Foreign key: user_id
     */
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Doctor's specialization (e.g., Cardiologist, Dermatologist).
     * Required field.
     */
    @Column(nullable = false)
    private String specialization;

    /**
     * Doctor's qualification (e.g., MBBS, MD).
     * Optional field.
     */
    private String qualification;

    /**
     * Total years of professional experience.
     * Optional field.
     */
    private Integer experienceYears;

    /**
     * Consultation fee charged by the doctor.
     * Optional field (can be used for filtering/sorting).
     */
    private Double consultationFee;

    /**
     * Availability status of the doctor.
     *
     * true  → Available for appointments
     * false → Not available
     *
     * Default value is true.
     */
    @Column(nullable = false)
    private boolean available = true;

    /**
     * Generates internalCode before saving if not already set.
     */
    @PrePersist
    public void ensureInternalCode() {
        if (getInternalCode() == null) {
            setInternalCode(CodeUtil.generateDoctorCode());
        }
    }
}
