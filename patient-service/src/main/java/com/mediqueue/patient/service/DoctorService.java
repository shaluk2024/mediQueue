package com.mediqueue.patient.service;

import com.mediqueue.patient.data.doctor.DoctorResponse;
import com.mediqueue.patient.data.doctor.UpdateDoctorRequest;
import com.mediqueue.patient.exception.ResourceNotFoundException;
import com.mediqueue.patient.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service responsible for managing doctor profile operations.
 *
 * Responsibilities:
 * - Fetch doctor profile by internal code
 * - Fetch logged-in doctor's own profile
 * - Search doctors by specialization
 * - List all available doctors
 * - Update doctor profile
 *
 * Access control:
 * - Public: search and view doctor profiles
 * - Doctor: update their own profile only
 * - Admin: update any doctor's profile
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {

    /** Repository for Doctor entity. */
    private final DoctorRepository doctorRepository;

    /**
     * Get doctor profile by internal code.
     *
     * Publicly accessible — patients use this during booking.
     *
     * @param doctorCode doctor's internal code (e.g. DOC-201-301)
     * @return DoctorResponse DTO
     * @throws ResourceNotFoundException if doctor not found
     */
    public DoctorResponse getDoctorByCode(final String doctorCode) {
        return doctorRepository.findByInternalCode(doctorCode)
                .map(DoctorResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found with code: " + doctorCode));
    }

    /**
     * Get the logged-in doctor's own profile.
     *
     * Uses X-User-Id header (injected by API Gateway) to
     * look up the doctor profile linked to the logged-in user.
     *
     * @param userCode logged-in user's internal code (from X-User-Id header)
     * @return DoctorResponse DTO
     * @throws ResourceNotFoundException if doctor profile not found for user
     */
    public DoctorResponse getMyProfile(final String userCode) {
        return doctorRepository.findByInternalCode(userCode)
                .map(DoctorResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor profile not found for user: " + userCode));
    }

    /**
     * Search available doctors by specialization.
     *
     * Publicly accessible — used in appointment booking flow.
     *
     * @param specialization specialization to search (case-insensitive)
     * @return List of matching available doctors
     */
    public List<DoctorResponse> searchBySpecialization(final String specialization) {
        return doctorRepository.findByAvailableTrueAndSpecializationContainingIgnoreCase(specialization)
                .stream()
                .map(DoctorResponse::from)
                .toList();
    }

    /**
     * Get all available doctors.
     *
     * Publicly accessible — used on doctor listing page.
     *
     * @return List of all available doctors
     */
    public List<DoctorResponse> getAllAvailableDoctors() {
        return doctorRepository.findByAvailableTrue()
                .stream()
                .map(DoctorResponse::from)
                .toList();
    }

    /**
     * Update doctor's own profile.
     *
     * Only non-null fields in the request are updated.
     * Only the logged-in doctor can update their own profile.
     *
     * @param userCode logged-in user's internal code (from X-User-Id header)
     * @param request  update request DTO with new values
     * @return updated DoctorResponse DTO
     * @throws ResourceNotFoundException if doctor profile not found
     */
    @Transactional
    @PreAuthorize("hasRole('DOCTOR')")
    public DoctorResponse updateMyProfile(final String userCode, final UpdateDoctorRequest request) {

        final var doctor = doctorRepository.findByInternalCode(userCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor profile not found for user: " + userCode));

        // Update only non-null fields
        if (request.getSpecialization() != null)  doctor.setSpecialization(request.getSpecialization());
        if (request.getQualification() != null)   doctor.setQualification(request.getQualification());
        if (request.getExperienceYears() != null) doctor.setExperienceYears(request.getExperienceYears());
        if (request.getConsultationFee() != null) doctor.setConsultationFee(request.getConsultationFee());
        if (request.getAvailable() != null)       doctor.setAvailable(request.getAvailable());

        doctorRepository.save(doctor);

        log.info("Doctor profile updated for user: {}", userCode);

        return DoctorResponse.from(doctor);
    }
}
