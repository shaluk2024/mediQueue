package com.mediqueue.patient.repository;

import com.mediqueue.patient.entity.Appointment;
import com.mediqueue.patient.entity.DoctorSlot;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing DoctorSlot entities.
 *
 * Provides:
 * - CRUD operations via JpaRepository
 * - Custom queries for slot availability and booking
 * - Concurrency control using optimistic locking
 *
 * Used in:
 * - Slot discovery (finding available slots)
 * - Appointment booking flow
 */
@Repository // Marks this as a Spring Data repository
public interface DoctorSlotRepository extends JpaRepository<DoctorSlot, String> {

    Optional<DoctorSlot> findByInternalCode(String code);

    /**
     * Fetch all available slots for a given doctor on a specific date.
     *
     * Filters:
     * - doctorId → specific doctor
     * - slotDate → specific date
     * - status = AVAILABLE → only free slots
     *
     * Used for:
     * - Showing available slots in UI
     * - Appointment booking selection
     *
     * @param doctorCode Code that uniquely identifies the doctor.
     * @param date     Date for which slots are needed
     * @return List of available slots
     */
    @Query("""
    SELECT s
    FROM DoctorSlot s
    JOIN FETCH s.doctor d
    WHERE d.internalCode = :doctorCode
    AND s.slotDate = :date
    AND s.status = com.mediqueue.patient.data.SlotStatus.AVAILABLE
    """)
    List<DoctorSlot> findAvailableSlots(@Param("doctorCode") final String doctorCode,
                                        @Param("date") final LocalDate date);

    /**
     * Fetch a specific slot for booking with optimistic locking.
     *
     * Key behavior:
     * - Applies OPTIMISTIC lock
     * - Loads version field (@Version in entity)
     * - Ensures safe concurrent booking
     *
     * Flow:
     * 1. Two users fetch same slot (version = 1)
     * 2. First user books → version becomes 2
     * 3. Second user tries to book → fails with OptimisticLockException
     *
     * Also ensures:
     * - Only AVAILABLE slots can be booked
     *
     * Used in:
     * - Critical booking transaction
     *
     * @param slotId ID of the slot
     * @return Optional containing slot if available
     */
    @Lock(LockModeType.OPTIMISTIC)
    @Query("""
        SELECT s
        FROM DoctorSlot s
        WHERE s.internalCode = :slotId
        AND s.status = com.mediqueue.patient.data.SlotStatus.AVAILABLE
    """)
    Optional<DoctorSlot> findAvailableSlotForBooking(@Param("slotId") final String slotId);
}
