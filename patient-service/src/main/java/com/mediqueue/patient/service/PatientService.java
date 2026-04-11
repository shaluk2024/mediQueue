package com.mediqueue.patient.service;

import com.mediqueue.patient.data.patient.PatientResponse;
import com.mediqueue.patient.data.patient.UpdatePatientRequest;
import com.mediqueue.patient.exception.ResourceNotFoundException;
import com.mediqueue.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for managing patient profile operations.
 *
 * Responsibilities:
 * - Fetch patient profile by internal code
 * - Fetch logged-in patient's own profile
 * - Update patient profile details
 *
 * Access control:
 * - Patient can only view/update their own profile
 * - Admin can view/update any patient's profile
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    /** Repository for Patient entity. */
    private final PatientRepository patientRepository;

    /**
     * Get patient profile by internal code.
     *
     * Accessible by:
     * - Admin (any patient)
     * - The patient themselves (via /me endpoint)
     *
     * @param patientCode patient's internal code (e.g. PAT-201-301)
     * @return PatientResponse DTO
     * @throws ResourceNotFoundException if patient not found
     */
    public PatientResponse getPatientByCode(final String patientCode) {
        return patientRepository.findByInternalCode(patientCode)
                .map(PatientResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with code: " + patientCode));
    }

    /**
     * Get the logged-in patient's own profile.
     *
     * Uses X-User-Id header (injected by API Gateway) to
     * look up the patient profile linked to the logged-in user.
     *
     * @param userCode logged-in user's internal code (from X-User-Id header)
     * @return PatientResponse DTO
     * @throws ResourceNotFoundException if patient profile not found for user
     */
    public PatientResponse getMyProfile(final String userCode) {
        return patientRepository.findByUser_InternalCode(userCode)
                .map(PatientResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient profile not found for user: " + userCode));
    }

    /**
     * Update patient profile.
     *
     * Only non-null fields in the request are updated.
     * Patient can only update their own profile.
     *
     * @param userCode logged-in user's internal code (from X-User-Id header)
     * @param request  update request DTO with new values
     * @return updated PatientResponse DTO
     * @throws ResourceNotFoundException if patient profile not found
     */
    @Transactional
    public PatientResponse updateMyProfile(final String userCode, final UpdatePatientRequest request) {

        final var patient = patientRepository.findByUser_InternalCode(userCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient profile not found for user: " + userCode));

        // Update only non-null fields
        if (request.getDateOfBirth() != null) patient.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null)      patient.setGender(request.getGender());
        if (request.getBloodGroup() != null)  patient.setBloodGroup(request.getBloodGroup());
        if (request.getMedicalHistory() != null) patient.setMedicalHistory(request.getMedicalHistory());

        patientRepository.save(patient);

        log.info("Patient profile updated for user: {}", userCode);

        return PatientResponse.from(patient);
    }
}
