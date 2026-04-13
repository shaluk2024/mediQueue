package com.mediqueue.patient.repository;

import com.mediqueue.patient.entity.Appointment;
import com.mediqueue.patient.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Doctor entities.
 *
 * Provides:
 * - Basic CRUD operations via JpaRepository
 * - Custom query methods for searching and filtering doctors
 *
 * Common use cases:
 * - Finding available doctors
 * - Searching by specialization
 * - Fetching doctor by associated user ID
 */
@Repository // Marks this as a Spring Data repository
public interface DoctorRepository extends JpaRepository<Doctor, String> {

    Optional<Doctor> findByInternalCode(String code);

    Optional<Doctor> findByUserInternalCode(String internalCode);

    /**
     * Fetch all doctors who are currently available.
     *
     * Used for:
     * - Patient doctor discovery
     * - Appointment booking flow
     *
     * @return List of available doctors
     */
    List<Doctor> findByAvailableTrue();

    /**
     * Search doctors by specialization (case-insensitive, partial match).
     *
     * Example:
     * Input: "cardio"
     * Matches: "Cardiologist"
     *
     * Used for:
     * - Search functionality in UI
     *
     * @param specialization Partial specialization keyword
     * @return List of matching doctors
     */
    List<Doctor> findBySpecializationContainingIgnoreCase(String specialization);

    /**
     * Fetch doctor using associated user ID.
     *
     * Useful when:
     * - Logged-in user is a doctor
     * - Need to fetch doctor profile from user context
     *
     * @param userId ID of the user
     * @return Optional containing Doctor if found
     */
    Optional<Doctor> findByUserId(String userId);


    // ================== 🔥 Recommended Additional Methods ==================

    /**
     * Fetch all available doctors by specialization.
     *
     * Combines availability + specialization filtering.
     *
     * @param specialization Specialization keyword
     * @return List of matching available doctors
     */
    List<Doctor> findByAvailableTrueAndSpecializationContainingIgnoreCase(String specialization);

    /**
     * Check if a doctor exists for a given user ID.
     *
     * Useful for:
     * - Validation during onboarding
     * - Preventing duplicate doctor profiles
     *
     * @param userId ID of the user
     * @return true if doctor exists, else false
     */
    boolean existsByUserId(String userId);

    /**
     * Fetch all doctors sorted by experience (descending).
     *
     * Useful for:
     * - Showing top experienced doctors
     *
     * @return List of doctors sorted by experience
     */
    List<Doctor> findAllByOrderByExperienceYearsDesc();
}