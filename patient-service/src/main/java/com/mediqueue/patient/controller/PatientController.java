package com.mediqueue.patient.controller;

import com.mediqueue.patient.data.patient.PatientResponse;
import com.mediqueue.patient.data.patient.UpdatePatientRequest;
import com.mediqueue.patient.docs.PatientApi;
import com.mediqueue.patient.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing patient profile operations.
 *
 * Exposes APIs for:
 * - Fetching logged-in patient's own profile
 * - Fetching any patient profile by internal code (Admin)
 * - Updating patient profile
 *
 * Base path: /api/patients
 */
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController implements PatientApi {

    /** Service layer handling patient business logic. */
    private final PatientService patientService;

    /**
     * Get logged-in patient's own profile.
     *
     * Endpoint: GET /api/patients/me
     *
     * X-User-Id header is injected by the API Gateway from the JWT token.
     *
     * @param userCode logged-in user's internal code (from header)
     * @return PatientResponse DTO
     */
    @GetMapping("/me")
    public ResponseEntity<PatientResponse> getMyProfile(
            @RequestHeader("X-User-Id") final String userCode) {

        final var response = patientService.getMyProfile(userCode);
        return ResponseEntity.ok(response);
    }

    /**
     * Get any patient's profile by internal code.
     *
     * Endpoint: GET /api/patients/{patientCode}
     *
     * Typically used by Admin.
     *
     * @param patientCode patient's internal code (e.g. PAT-201-301)
     * @return PatientResponse DTO
     */
    @GetMapping("/{patientCode}")
    public ResponseEntity<PatientResponse> getPatientByCode(
            @PathVariable("patientCode") final String patientCode) {

        final var response = patientService.getPatientByCode(patientCode);
        return ResponseEntity.ok(response);
    }

    /**
     * Update logged-in patient's own profile.
     *
     * Endpoint: PUT /api/patients/me
     *
     * Only non-null fields in the request body are updated.
     * X-User-Id header is injected by the API Gateway from the JWT token.
     *
     * @param userCode logged-in user's internal code (from header)
     * @param request  update request DTO
     * @return updated PatientResponse DTO
     */
    @PutMapping("/me")
    public ResponseEntity<PatientResponse> updateMyProfile(
            @RequestHeader("X-User-Id") final String userCode,
            @RequestBody final UpdatePatientRequest request) {

        final var response = patientService.updateMyProfile(userCode, request);
        return ResponseEntity.ok(response);
    }
}