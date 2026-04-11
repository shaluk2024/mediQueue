package com.mediqueue.patient.repository;

import com.mediqueue.patient.entity.Appointment;
import com.mediqueue.patient.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing User entities.
 *
 * Extends JpaRepository to provide:
 * - Basic CRUD operations (save, findById, delete, etc.)
 * - Pagination and sorting support
 *
 * Custom methods are defined for:
 * - Authentication (finding user by email)
 * - Validation (checking if email already exists)
 */
@Repository // Marks this as a Spring Data repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByInternalCode( String code);

    /**
     * Fetch a user by email.
     *
     * Used for:
     * - Login/authentication
     * - Loading user details for JWT generation
     *
     * Returns Optional to safely handle cases where user is not found.
     *
     * @param email User's email address
     * @return Optional containing User if found
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Check if a user already exists with the given email.
     *
     * Used for:
     * - Registration validation (prevent duplicate accounts)
     * - Ensuring email uniqueness
     *
     * More efficient than fetching full entity (uses COUNT/EXISTS internally).
     *
     * @param email User's email address
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);
}
