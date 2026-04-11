package com.mediqueue.patient.controller;

import com.mediqueue.patient.data.doctor.DoctorResponse;
import com.mediqueue.patient.data.doctor.UpdateDoctorRequest;
import com.mediqueue.patient.docs.DoctorApi;
import com.mediqueue.patient.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing doctor profile operations.
 *
 * Exposes APIs for:
 * - Fetching logged-in doctor's own profile
 * - Fetching any doctor profile by internal code
 * - Searching doctors by specialization
 * - Listing all available doctors
 * - Updating doctor profile
 *
 * Base path: /api/doctors
 */
@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController implements DoctorApi {

    /** Service layer handling doctor business logic. */
    private final DoctorService doctorService;

    /**
     * Get logged-in doctor's own profile.
     *
     * Endpoint: GET /api/doctors/me
     *
     * X-User-Id header is injected by the API Gateway from the JWT token.
     *
     * @param userCode logged-in user's internal code (from header)
     * @return DoctorResponse DTO
     */
    @GetMapping("/me")
    public ResponseEntity<DoctorResponse> getMyProfile(
            @RequestHeader("X-User-Id") final String userCode) {

        final var response = doctorService.getMyProfile(userCode);
        return ResponseEntity.ok(response);
    }

    /**
     * Get any doctor's profile by internal code.
     *
     * Endpoint: GET /api/doctors/{doctorCode}
     *
     * Publicly accessible — used by patients during booking.
     *
     * @param doctorCode doctor's internal code (e.g. DOC-201-301)
     * @return DoctorResponse DTO
     */
    @GetMapping("/{doctorCode}")
    public ResponseEntity<DoctorResponse> getDoctorByCode(
            @PathVariable("doctorCode") final String doctorCode) {

        final var response = doctorService.getDoctorByCode(doctorCode);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all available doctors.
     *
     * Endpoint: GET /api/doctors
     *
     * Publicly accessible — used on doctor listing page.
     *
     * @return List of all available doctors
     */
    @GetMapping
    public ResponseEntity<List<DoctorResponse>> getAllAvailableDoctors() {

        final var response = doctorService.getAllAvailableDoctors();
        return ResponseEntity.ok(response);
    }

    /**
     * Search doctors by specialization.
     *
     * Endpoint: GET /api/doctors/search?specialization=Cardiology
     *
     * Publicly accessible — used in appointment booking flow.
     *
     * @param specialization specialization to search (case-insensitive)
     * @return List of matching available doctors
     */
    @GetMapping("/search")
    public ResponseEntity<List<DoctorResponse>> searchBySpecialization(
            @RequestParam("specialization") final String specialization) {

        final var response = doctorService.searchBySpecialization(specialization);
        return ResponseEntity.ok(response);
    }

    /**
     * Update logged-in doctor's own profile.
     *
     * Endpoint: PUT /api/doctors/me
     *
     * Only non-null fields in the request body are updated.
     * X-User-Id header is injected by the API Gateway from the JWT token.
     *
     * @param userCode logged-in user's internal code (from header)
     * @param request  update request DTO
     * @return updated DoctorResponse DTO
     */
    @PutMapping("/me")
    public ResponseEntity<DoctorResponse> updateMyProfile(
            @RequestHeader("X-User-Id") final String userCode,
            @RequestBody final UpdateDoctorRequest request) {

        final var response = doctorService.updateMyProfile(userCode, request);
        return ResponseEntity.ok(response);
    }
}