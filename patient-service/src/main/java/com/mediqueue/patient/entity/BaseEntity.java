package com.mediqueue.patient.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Base entity for all relational entities.
 *
 * Provides:
 * - Internal DB ID (primary key)
 * - Public identifier (internalCode)
 * - Audit fields (createdAt, updatedAt)
 * - Versioning for optimistic locking
 *
 * All entities should extend this class.
 */
@MappedSuperclass // Not a table, but fields are inherited by child entities
@Getter
@Setter
public abstract class BaseEntity {

    /**
     * Primary key (internal use only).
     * Not exposed in APIs.
     */
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    protected Long id;

    /**
     * Public identifier exposed in APIs.
     * Used instead of ID to avoid exposing DB internals.
     */
    @Column(name = "internal_code", unique = true, nullable = false, updatable = false)
    protected String internalCode;

    /**
     * Timestamp when entity was created.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    protected LocalDateTime createdAt;

    /**
     * Timestamp when entity was last updated.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    protected LocalDateTime updatedAt;

}
