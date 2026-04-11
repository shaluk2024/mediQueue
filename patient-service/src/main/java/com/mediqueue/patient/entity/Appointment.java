package com.mediqueue.patient.entity;

import com.mediqueue.patient.data.AppointmentStatus;
import com.mediqueue.patient.data.TriagePriority;
import com.mediqueue.patient.util.CodeUtil;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing an appointment between a patient and a doctor.
 *
 * This is the core entity of the system and is responsible for:
 * - Linking patient, doctor, and selected time slot
 * - Tracking appointment lifecycle (confirmed, canceled, completed, etc.)
 * - Storing AI-based triage results
 * - Managing queue position for consultations
 *
 * Acts as a central point for:
 * - Booking system
 * - Queue management
 * - AI triage integration
 */
@Entity // Marks this as a JPA entity
@Table(name = "appointments") // Maps to "appointments" table
@Getter @Setter // Lombok: generates getters and setters
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Lombok: no-args constructor
@AllArgsConstructor(access = AccessLevel.PRIVATE) // Lombok: all-args constructor
@Builder // Lombok: builder pattern support
public class Appointment extends BaseEntity{

    /**
     * Primary key for the appointment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "appointments_seq_gen")
    @SequenceGenerator(
            name = "appointments_seq_gen",
            sequenceName = "appointments_id_seq",
            allocationSize = 1
    )
    private Long id;

    /**
     * Many-to-one relationship with User (patient).
     *
     * A patient can have multiple appointments.
     * Lazy loading improves performance.
     *
     * Foreign key: patient_id
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    /**
     * Many-to-one relationship with Doctor.
     *
     * A doctor can have multiple appointments.
     *
     * Foreign key: doctor_id
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    /**
     * One-to-one relationship with DoctorSlot.
     *
     * Each appointment is associated with exactly one slot.
     * Ensures a slot is booked only once.
     *
     * Foreign key: slot_id
     */
    @OneToOne
    @JoinColumn(name = "slot_id", nullable = false)
    private DoctorSlot slot;

    /**
     * Symptoms described by the patient.
     * Used for AI triage and doctor's reference.
     */
    private String symptoms;

    /**
     * Status of the appointment.
     *
     * CONFIRMED → Appointment is booked
     * CANCELLED → Appointment canceled by user/system
     * COMPLETED → Consultation finished
     * NO_SHOW   → Patient did not show up
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status = AppointmentStatus.CONFIRMED;

    /**
     * AI-based triage priority.
     *
     * Determines urgency of the case:
     * LOW, MEDIUM, HIGH, CRITICAL
     *
     * Can be used for queue prioritization.
     */
    @Enumerated(EnumType.STRING)
    private TriagePriority triagePriority;

    /**
     * Explanation/reasoning provided by AI for triage decision.
     * Useful for transparency and debugging AI output.
     */
    private String triageReasoning;

    /**
     * Position of the patient in the doctor's queue.
     *
     * Lower number = higher priority.
     * Can be influenced by triage priority.
     */
    private Integer queuePosition;

    /**
     * Timestamp when the appointment was booked.
     * Automatically populated by Hibernate.
     */
    @CreationTimestamp
    private LocalDateTime bookedAt;

    /**
     * Generates internalCode before saving if not already set.
     */
    @PrePersist
    public void ensureInternalCode() {
        if (getInternalCode() == null) {
            setInternalCode(CodeUtil.generateAppointmentCode());
        }
    }

}
