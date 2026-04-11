-- ============================================================
-- SEQUENCES
-- allocationSize = 1 in all entities
-- DB sequence increments by 1 to stay in sync
-- ============================================================

CREATE SEQUENCE IF NOT EXISTS users_id_seq
    START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS patients_id_seq
    START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS doctors_id_seq
    START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS doctor_slots_id_seq
    START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS appointments_id_seq
    START WITH 1 INCREMENT BY 1;


-- ============================================================
-- USERS TABLE
-- Base identity and authentication table.
-- All roles (PATIENT, DOCTOR, ADMIN) are stored here.
-- BaseEntity fields: internal_code, created_at, updated_at
-- ============================================================

CREATE TABLE IF NOT EXISTS users (
    id            BIGINT       PRIMARY KEY DEFAULT nextval('users_id_seq'),

    -- BaseEntity fields
    internal_code VARCHAR(15)  NOT NULL UNIQUE,           -- e.g. PAT-123-456, DOC-765-890
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,

    -- User specific fields
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(100) NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    phone         VARCHAR(15),
    role          VARCHAR(20)  NOT NULL                   -- PATIENT, DOCTOR, ADMIN
    );


-- ============================================================
-- PATIENTS TABLE
-- Patient-specific profile data.
-- user_id FK links to users table (one-to-one).
-- BaseEntity fields: internal_code, created_at, updated_at
-- ============================================================

CREATE TABLE IF NOT EXISTS patients (
    id              BIGINT      PRIMARY KEY DEFAULT nextval('patients_id_seq'),

    -- BaseEntity fields
    internal_code   VARCHAR(15) NOT NULL UNIQUE,
    created_at      TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,

    -- Patient specific fields
    user_id         BIGINT      NOT NULL UNIQUE REFERENCES users(id),
    date_of_birth   DATE,
    gender          VARCHAR(10),                          -- MALE, FEMALE, OTHER
    blood_group     VARCHAR(5),                           -- A+, B+, O+, AB+
    medical_history TEXT
    );

-- Index: frequently queried to fetch patient by logged-in user
CREATE INDEX IF NOT EXISTS idx_patient_user_id
    ON patients(user_id);

-- Index: useful for emergency blood group filtering
CREATE INDEX IF NOT EXISTS idx_patient_blood_group
    ON patients(blood_group);


-- ============================================================
-- DOCTORS TABLE
-- Doctor-specific professional profile data.
-- user_id FK links to users table (one-to-one).
-- BaseEntity fields: internal_code, created_at, updated_at
-- ============================================================

CREATE TABLE IF NOT EXISTS doctors (
    id                BIGINT           PRIMARY KEY DEFAULT nextval('doctors_id_seq'),

    -- BaseEntity fields
    internal_code     VARCHAR(15)      NOT NULL UNIQUE,
    created_at        TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,

    -- Doctor specific fields
    user_id           BIGINT           NOT NULL UNIQUE REFERENCES users(id),
    specialization    VARCHAR(100)     NOT NULL,
    qualification     VARCHAR(100),
    experience_years  INT,
    consultation_fee  DOUBLE PRECISION,
    available         BOOLEAN          NOT NULL DEFAULT TRUE
    );

-- Index: frequently filtered by specialization during doctor search
CREATE INDEX IF NOT EXISTS idx_doctor_specialization
    ON doctors(specialization);

-- Index: filtered when showing only available doctors
CREATE INDEX IF NOT EXISTS idx_doctor_available
    ON doctors(available);


-- ============================================================
-- DOCTOR_SLOTS TABLE
-- Represents a doctor's available time slot.
-- BaseEntity fields: internal_code, created_at, updated_at
-- version column ONLY here — optimistic locking for double booking prevention
-- ============================================================

CREATE TABLE IF NOT EXISTS doctor_slots (
    id            BIGINT      PRIMARY KEY DEFAULT nextval('doctor_slots_id_seq'),

    -- BaseEntity fields
    internal_code VARCHAR(15) NOT NULL UNIQUE,
    created_at    TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,

    -- Optimistic locking — only on this table
    version       BIGINT      NOT NULL DEFAULT 0,

    -- DoctorSlot specific fields
    doctor_id     BIGINT      NOT NULL REFERENCES doctors(id),
    slot_date     DATE        NOT NULL,
    start_time    TIME        NOT NULL,
    end_time      TIME        NOT NULL,
    status        VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' -- AVAILABLE, BOOKED, CANCELLED
    );

-- Composite index: fetch slots for a doctor on a given date (most common query)
CREATE INDEX IF NOT EXISTS idx_slot_doctor_date
    ON doctor_slots(doctor_id, slot_date);

-- Index: filter slots by status (AVAILABLE slots listing)
CREATE INDEX IF NOT EXISTS idx_slot_status
    ON doctor_slots(status);


-- ============================================================
-- APPOINTMENTS TABLE
-- Core entity linking patient, doctor, and slot.
-- Tracks full appointment lifecycle and AI triage results.
-- BaseEntity fields: internal_code, created_at, updated_at
-- booked_at is a separate business field — not same as created_at
-- ============================================================

CREATE TABLE IF NOT EXISTS appointments (
    id                BIGINT      PRIMARY KEY DEFAULT nextval('appointments_id_seq'),

    -- BaseEntity fields
    internal_code     VARCHAR(15) NOT NULL UNIQUE,
    created_at        TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,

    -- Appointment specific fields
    patient_id        BIGINT      NOT NULL REFERENCES users(id),
    doctor_id         BIGINT      NOT NULL REFERENCES doctors(id),
    slot_id           BIGINT      NOT NULL UNIQUE REFERENCES doctor_slots(id),
    symptoms          TEXT,
    status            VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED', -- CONFIRMED, CANCELLED, COMPLETED, NO_SHOW
    triage_priority   VARCHAR(20),                              -- LOW, MEDIUM, HIGH, CRITICAL
    triage_reasoning  TEXT,
    queue_position    INT,
    booked_at         TIMESTAMP   DEFAULT CURRENT_TIMESTAMP     -- business field, separate from created_at
    );

-- Index: fetch all appointments for a patient (patient dashboard)
CREATE INDEX IF NOT EXISTS idx_appointment_patient_id
    ON appointments(patient_id);

-- Index: fetch all appointments for a doctor (doctor dashboard)
CREATE INDEX IF NOT EXISTS idx_appointment_doctor_id
    ON appointments(doctor_id);

-- Composite index: fetch appointments for a doctor filtered by status
-- Most common query for queue management
CREATE INDEX IF NOT EXISTS idx_appointment_doctor_status
    ON appointments(doctor_id, status);

-- Index: sort/filter by triage priority for queue ordering
CREATE INDEX IF NOT EXISTS idx_appointment_triage_priority
    ON appointments(triage_priority);