package com.mediqueue.patient.entity;

import com.mediqueue.patient.data.SlotStatus;
import com.mediqueue.patient.util.CodeUtil;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entity representing a doctor's available time slot.
 *
 * This table is used to:
 * - Define when a doctor is available
 * - Manage appointment booking
 * - Prevent double booking using optimistic locking
 *
 * Key features:
 * - Indexed for fast lookup (doctor + date, status)
 * - Uses optimistic locking (@Version) to handle concurrency
 */
@Entity // Marks this as a JPA entity
@Table(name = "doctor_slots", indexes = {

        /**
         * Composite index to quickly fetch slots for a doctor on a given date.
         * Useful for appointment booking queries.
         */
        @Index(name = "idx_slot_doctor_date", columnList = "doctor_id, slot_date"),

        /**
         * Index on status for filtering available/booked slots efficiently.
         */
        @Index(name = "idx_slot_status", columnList = "status")
})
@Getter @Setter // Lombok: generates getters and setters
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Lombok: no-args constructor
@AllArgsConstructor(access = AccessLevel.PRIVATE) // Lombok: all-args constructor
@Builder // Lombok: builder pattern support
public class DoctorSlot extends BaseEntity{

    /**
     * Primary key for the slot.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "doctor_slots_seq_gen")
    @SequenceGenerator(
            name = "doctor_slots_seq_gen",
            sequenceName = "doctor_slots_id_seq",
            allocationSize = 1
    )
    private Long id;

    /**
     * Many-to-one relationship with Doctor.
     *
     * A doctor can have multiple slots.
     * Lazy loading is used to improve performance.
     *
     * Foreign key: doctor_id
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    /**
     * Date of the slot (e.g., 2026-04-10).
     * Required field.
     */
    @Column(nullable = false)
    private LocalDate slotDate;

    /**
     * Start time of the slot (e.g., 10:00 AM).
     * Required field.
     */
    @Column(nullable = false)
    private LocalTime startTime;

    /**
     * End time of the slot (e.g., 10:30 AM).
     * Required field.
     */
    @Column(nullable = false)
    private LocalTime endTime;

    /**
     * Status of the slot.
     *
     * AVAILABLE → Slot is free for booking
     * BOOKED    → Slot is already booked
     * CANCELLED → Slot is cancelled/unavailable
     *
     * Stored as STRING in DB.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SlotStatus status = SlotStatus.AVAILABLE;

    /**
     * Version field used for optimistic locking.
     *
     * Prevents double booking in concurrent scenarios:
     * - Two users try to book the same slot
     * - Both read version = 1
     * - First update succeeds → version becomes 2
     * - Second update fails (version mismatch)
     *
     * Throws OptimisticLockException
     */
    @Version
    private Long version;

    /**
     * Generates internalCode before saving if not already set.
     */
    @PrePersist
    public void ensureInternalCode() {
        if (getInternalCode() == null) {
            setInternalCode(CodeUtil.generateSlotCode());
        }
    }

}
