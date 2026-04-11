package com.mediqueue.patient.repository;

import com.mediqueue.patient.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing Patient entities.
 *
 * Extends JpaRepository to provide standard CRUD operations.
 *
 * Custom queries for:
 * - Fetching patient by internal code
 * - Fetching patient by linked user ID
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Find patient by their unique internal code.
     *
     * Used for:
     * - Patient profile view
     * - Profile update
     *
     * @param internalCode patient's internal code (e.g. PAT-201-301)
     * @return Optional of Patient
     */
    Optional<Patient> findByInternalCode(String internalCode);

    /**
     * Find patient by linked user's internal code.
     *
     * Used for:
     * - Fetching logged-in patient's profile via X-User-Id header
     *
     * @param userInternalCode user's internal code (e.g. USR-101-201)
     * @return Optional of Patient
     */
    Optional<Patient> findByUser_InternalCode(String userInternalCode);
}