package com.mediqueue.patient.repository;

import com.mediqueue.patient.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Appointment entities.
 *
 * Extends JpaRepository to provide CRUD operations and pagination support.
 *
 * Custom queries are defined for:
 * - Fetching patient appointment history
 * - Building doctor's queue for a specific date
 * - Counting confirmed appointments (used for capacity/analytics)
 */
@Repository // Marks this as a Spring Data repository
public interface AppointmentRepository extends JpaRepository<Appointment, String> {

    Optional<Appointment> findByInternalCode(String code);

    /**
     * Fetch all appointments for a given patient, ordered by booking time (latest first).
     *
     * Used for:
     * - Patient dashboard
     * - Appointment history view
     *
     * @param patientCode ID of the patient (User)
     * @return List of appointments sorted by bookedAt descending
     */
    @Query("""
    SELECT a FROM Appointment a 
    WHERE a.patient.internalCode = :patientCode 
    ORDER BY a.bookedAt DESC
    """)
    List<Appointment> findAllByPatientCode(
            @Param("patientCode") String patientCode
    );
    /**
     * Fetch the doctor's queue for a specific date.
     *
     * Key features:
     * - Fetches patient and slot using JOIN FETCH (avoids N+1 problem)
     * - Filters only CONFIRMED appointments
     * - Orders by:
     *      1. triagePriority (DESC → CRITICAL first)
     *      2. queuePosition (ASC → lower number first)
     *
     * Used for:
     * - Doctor dashboard
     * - Queue management system
     * - Real-time consultation order
     *
     * @param doctorCode Code that uniquely identifies the doctor in the system.
     * @param date     Date for which queue is needed
     * @return Ordered list representing doctor's queue
     */
    @Query("""
        SELECT a FROM Appointment a
        JOIN FETCH a.patient
        JOIN FETCH a.slot s
        WHERE a.doctor.internalCode = :doctorCode
        AND s.slotDate = :date
        AND a.status = com.mediqueue.patient.data.AppointmentStatus.CONFIRMED
        ORDER BY a.triagePriority DESC, a.queuePosition ASC
    """)
    List<Appointment> findDoctorQueueForDate(@Param("doctorCode") String doctorCode, @Param("date")LocalDate date);


    /**
     * Count total confirmed appointments for a doctor on a given date.
     *
     * Used for:
     * - Capacity planning
     * - Slot availability checks
     * - Analytics/monitoring
     *
     * @param doctorId ID of the doctor
     * @param date     Date for which count is required
     * @return Number of confirmed appointments
     */
    @Query("""
        SELECT COUNT(a)
        FROM Appointment a
        WHERE a.doctor.id = :doctorId
        AND a.slot.slotDate = :date
        AND a.status = com.mediqueue.patient.data.AppointmentStatus.CONFIRMED
    """)
    long countConfirmedForDoctorOnDate(
            @Param("doctorId") long doctorId,
            @Param("date") LocalDate date
    );
}
