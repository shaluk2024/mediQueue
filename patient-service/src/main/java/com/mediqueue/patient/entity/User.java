package com.mediqueue.patient.entity;

import com.mediqueue.patient.data.Role;
import com.mediqueue.patient.util.CodeUtil;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a user in the system.
 *
 * This table stores:
 * - Authentication details (email, password)
 * - User profile (name, phone)
 * - Authorization role (PATIENT, DOCTOR, ADMIN)
 * - Metadata (created timestamp)
 */
@Entity // Marks this class as a JPA entity (mapped to DB table)
@Table(name = "users") // Maps to "users" table in database
@Getter @Setter // Lombok: generates getters and setters
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Lombok: no-args constructor
@AllArgsConstructor // Lombok: all-args constructor
@Builder // Lombok: builder pattern support
public class User extends BaseEntity {

    /**
     * Primary key for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq_gen")
    @SequenceGenerator(
            name = "users_seq_gen",
            sequenceName = "users_id_seq",
            allocationSize = 1
    )
    private Long id;

    /**
     * User's email (used for login).
     * Must be unique and cannot be null.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Encrypted password (BCrypt hashed).
     * Never store plain text passwords.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Full name of the user.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Optional phone number.
     */
    private String phone;

    /**
     * Role of the user (authorization).
     * Stored as STRING in DB (e.g., "ADMIN", "DOCTOR").
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * Timestamp when the user was created.
     * Automatically populated by Hibernate.
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Generates internalCode before saving if not already set.
     */
    @PrePersist
    public void ensureInternalCode() {
        if (getInternalCode() == null) {
            setInternalCode(CodeUtil.generateUserCode());
        }
    }

}
