-- ============================================================
-- SAMPLE DATA FOR MEDIQUEUE
-- Order of insertion matters due to FK constraints:
-- users → patients, doctors → doctor_slots → appointments
-- All passwords are BCrypt encoded for: 'password123'
-- Sequence increment = 1, so IDs are: 1, 2, 3, 4...
-- BaseEntity fields: internal_code, created_at, updated_at
-- version column only in doctor_slots
-- ============================================================


-- ============================================================
-- USERS
-- ID assignment (increment by 1):
-- 1 → Admin User
-- 2 → Rahul Gupta      (PATIENT)
-- 3 → Sneha Joshi      (PATIENT)
-- 4 → Arjun Mehta      (PATIENT)
-- 5 → Priya Nair       (PATIENT)
-- 6 → Dr. Rajesh Sharma (DOCTOR)
-- 7 → Dr. Priya Verma  (DOCTOR)
-- 8 → Dr. Amit Singh   (DOCTOR)
-- 9 → Dr. Kavita Rao   (DOCTOR)
-- ============================================================

INSERT INTO users (id, internal_code, created_at, updated_at, name, email, password, phone, role) VALUES

-- Admin
(nextval('users_id_seq'), 'ADM-100-100', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
 'Admin User', 'admin@mediqueue.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KJB.C', '9999900000', 'ADMIN'),

-- Patients
(nextval('users_id_seq'), 'PAT-101-201', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
 'Rahul Gupta', 'rahul.gupta@gmail.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KJB.C', '9876501001', 'PATIENT'),

(nextval('users_id_seq'), 'PAT-102-202', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
 'Sneha Joshi', 'sneha.joshi@gmail.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KJB.C', '9876501002', 'PATIENT'),

(nextval('users_id_seq'), 'PAT-103-203', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
 'Arjun Mehta', 'arjun.mehta@gmail.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KJB.C', '9876501003', 'PATIENT'),

(nextval('users_id_seq'), 'PAT-104-204', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
 'Priya Nair', 'priya.nair@gmail.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KJB.C', '9876501004', 'PATIENT'),

-- Doctors
(nextval('users_id_seq'), 'DOC-101-201', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
 'Dr. Rajesh Sharma', 'rajesh.sharma@mediqueue.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KJB.C', '9876502001', 'DOCTOR'),

(nextval('users_id_seq'), 'DOC-102-202', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
 'Dr. Priya Verma', 'priya.verma@mediqueue.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KJB.C', '9876502002', 'DOCTOR'),

(nextval('users_id_seq'), 'DOC-103-203', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
 'Dr. Amit Singh', 'amit.singh@mediqueue.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KJB.C', '9876502003', 'DOCTOR'),

(nextval('users_id_seq'), 'DOC-104-204', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
 'Dr. Kavita Rao', 'kavita.rao@mediqueue.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KJB.C', '9876502004', 'DOCTOR');


-- ============================================================
-- PATIENTS
-- user_id: Rahul=2, Sneha=3, Arjun=4, Priya=5
-- patients.id → 1, 2, 3, 4
-- ============================================================

INSERT INTO patients (id, internal_code, created_at, updated_at, user_id, date_of_birth, gender, blood_group, medical_history) VALUES

                                                                                                                                   (nextval('patients_id_seq'), 'PAT-201-301', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
                                                                                                                                    2, '1995-06-15', 'MALE',   'O+',  'No known allergies'),

                                                                                                                                   (nextval('patients_id_seq'), 'PAT-202-302', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
                                                                                                                                    3, '1998-03-22', 'FEMALE', 'B+',  'Mild asthma, allergic to penicillin'),

                                                                                                                                   (nextval('patients_id_seq'), 'PAT-203-303', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
                                                                                                                                    4, '1990-11-05', 'MALE',   'A+',  'Diabetic, on insulin'),

                                                                                                                                   (nextval('patients_id_seq'), 'PAT-204-304', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
                                                                                                                                    5, '2000-07-18', 'FEMALE', 'AB+', 'No known conditions');


-- ============================================================
-- DOCTORS
-- user_id: Dr.Rajesh=6, Dr.Priya=7, Dr.Amit=8, Dr.Kavita=9
-- doctors.id → 1, 2, 3, 4
-- ============================================================

INSERT INTO doctors (id, internal_code, created_at, updated_at, user_id, specialization, qualification, experience_years, consultation_fee, available) VALUES

                                                                                                                                                           (nextval('doctors_id_seq'), 'DOC-201-301', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
                                                                                                                                                            6, 'Cardiology',  'MD',   10, 800.00,  true),

                                                                                                                                                           (nextval('doctors_id_seq'), 'DOC-202-302', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
                                                                                                                                                            7, 'Neurology',   'DM',   8,  1000.00, true),

                                                                                                                                                           (nextval('doctors_id_seq'), 'DOC-203-303', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
                                                                                                                                                            8, 'Orthopedics', 'MS',   12, 700.00,  true),

                                                                                                                                                           (nextval('doctors_id_seq'), 'DOC-204-304', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
                                                                                                                                                            9, 'Dermatology', 'MBBS', 5,  500.00,  true);


-- ============================================================
-- DOCTOR SLOTS
-- doctor_id: Rajesh=1, Priya=2, Amit=3, Kavita=4
-- slot ids: 1 through 17
-- version included ✅ — optimistic locking only on this table
-- ============================================================

INSERT INTO doctor_slots (id, internal_code, version, created_at, updated_at, doctor_id, slot_date, start_time, end_time, status) VALUES

-- Dr. Rajesh Sharma - Cardiology (slot ids: 1-6)
(nextval('doctor_slots_id_seq'), 'SLT-101-201', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, CURRENT_DATE,     '09:00', '09:30', 'BOOKED'),
(nextval('doctor_slots_id_seq'), 'SLT-102-202', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, CURRENT_DATE,     '09:30', '10:00', 'BOOKED'),
(nextval('doctor_slots_id_seq'), 'SLT-103-203', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, CURRENT_DATE,     '10:00', '10:30', 'AVAILABLE'),
(nextval('doctor_slots_id_seq'), 'SLT-104-204', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, CURRENT_DATE,     '10:30', '11:00', 'AVAILABLE'),
(nextval('doctor_slots_id_seq'), 'SLT-105-205', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, CURRENT_DATE + 1, '09:00', '09:30', 'AVAILABLE'),
(nextval('doctor_slots_id_seq'), 'SLT-106-206', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, CURRENT_DATE + 1, '09:30', '10:00', 'AVAILABLE'),

-- Dr. Priya Verma - Neurology (slot ids: 7-10)
(nextval('doctor_slots_id_seq'), 'SLT-107-207', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, CURRENT_DATE,     '10:00', '10:30', 'BOOKED'),
(nextval('doctor_slots_id_seq'), 'SLT-108-208', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, CURRENT_DATE,     '10:30', '11:00', 'AVAILABLE'),
(nextval('doctor_slots_id_seq'), 'SLT-109-209', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, CURRENT_DATE,     '11:00', '11:30', 'AVAILABLE'),
(nextval('doctor_slots_id_seq'), 'SLT-110-210', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, CURRENT_DATE + 1, '10:00', '10:30', 'AVAILABLE'),

-- Dr. Amit Singh - Orthopedics (slot ids: 11-14)
(nextval('doctor_slots_id_seq'), 'SLT-111-211', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, CURRENT_DATE,     '14:00', '14:30', 'BOOKED'),
(nextval('doctor_slots_id_seq'), 'SLT-112-212', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, CURRENT_DATE,     '14:30', '15:00', 'AVAILABLE'),
(nextval('doctor_slots_id_seq'), 'SLT-113-213', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, CURRENT_DATE,     '15:00', '15:30', 'AVAILABLE'),
(nextval('doctor_slots_id_seq'), 'SLT-114-214', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, CURRENT_DATE + 1, '14:00', '14:30', 'AVAILABLE'),

-- Dr. Kavita Rao - Dermatology (slot ids: 15-17)
(nextval('doctor_slots_id_seq'), 'SLT-115-215', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 4, CURRENT_DATE,     '11:00', '11:30', 'AVAILABLE'),
(nextval('doctor_slots_id_seq'), 'SLT-116-216', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 4, CURRENT_DATE,     '11:30', '12:00', 'AVAILABLE'),
(nextval('doctor_slots_id_seq'), 'SLT-117-217', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 4, CURRENT_DATE + 1, '11:00', '11:30', 'AVAILABLE');


-- ============================================================
-- APPOINTMENTS
-- patient_id → users.id: Rahul=2, Sneha=3, Arjun=4, Priya=5
-- doctor_id  → doctors.id: Rajesh=1, Priya=2, Amit=3
-- slot_id    → BOOKED slots only: 1, 2, 7, 11
-- ============================================================

INSERT INTO appointments (id, internal_code, created_at, updated_at, patient_id, doctor_id, slot_id, symptoms, status, triage_priority, triage_reasoning, queue_position, booked_at) VALUES

-- Rahul → Dr. Rajesh → slot 1
(nextval('appointments_id_seq'), 'APT-101-201', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
 2, 1, 1,
 'Chest pain and shortness of breath since 2 days',
 'CONFIRMED', 'HIGH',
 'Symptoms suggest possible cardiac involvement. Immediate consultation recommended.',
 1, CURRENT_TIMESTAMP),

-- Sneha → Dr. Rajesh → slot 2
(nextval('appointments_id_seq'), 'APT-102-202', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
 3, 1, 2,
 'Mild chest discomfort after exercise',
 'CONFIRMED', 'MEDIUM',
 'Symptoms are exercise-induced and mild. Monitor and consult.',
 2, CURRENT_TIMESTAMP),

-- Arjun → Dr. Priya Verma → slot 7
(nextval('appointments_id_seq'), 'APT-103-203', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
 4, 2, 7,
 'Severe headache and blurred vision for 3 days',
 'CONFIRMED', 'HIGH',
 'Neurological symptoms warrant urgent evaluation.',
 1, CURRENT_TIMESTAMP),

-- Priya N → Dr. Amit → slot 11
(nextval('appointments_id_seq'), 'APT-104-204', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
 5, 3, 11,
 'Knee pain after sports injury',
 'CONFIRMED', 'LOW',
 'Musculoskeletal injury, non-urgent. Rest and consultation advised.',
 1, CURRENT_TIMESTAMP);