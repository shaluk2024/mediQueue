package com.mediqueue.patient.controller;

import com.mediqueue.patient.data.appointment.SlotResponse;
import com.mediqueue.patient.data.appointment.CreateSlotRequest;
import com.mediqueue.patient.docs.DoctorSlotApi;
import com.mediqueue.patient.service.DoctorSlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for managing doctor slot operations.
 *
 * Exposes APIs for:
 * - Creating a new slot (Admin only)
 * - Deleting a slot (Admin only)
 * - Fetching all slots for a doctor on a date
 *
 * Base path: /api/slots
 */
@RestController
@RequestMapping("/api/slots")
@RequiredArgsConstructor
public class DoctorSlotController implements DoctorSlotApi {

    /** Service layer handling slot business logic. */
    private final DoctorSlotService doctorSlotService;

    /**
     * Create a new slot for a doctor.
     *
     * Endpoint: POST /api/slots
     *
     * Only accessible by ADMIN.
     *
     * @param request create slot request DTO
     * @return created SlotResponse DTO
     */
    @PostMapping
    public ResponseEntity<SlotResponse> createSlot(
            @Valid @RequestBody final CreateSlotRequest request) {

        final var response = doctorSlotService.createSlot(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Delete a slot by internal code.
     *
     * Endpoint: DELETE /api/slots/{slotCode}
     *
     * Only accessible by ADMIN.
     * Only AVAILABLE slots can be deleted.
     *
     * @param slotCode slot's internal code (e.g. SLT-101-201)
     * @return HTTP 204 No Content on success
     */
    @DeleteMapping("/{slotCode}")
    public ResponseEntity<Void> deleteSlot(
            @PathVariable("slotCode") final String slotCode) {

        doctorSlotService.deleteSlot(slotCode);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all slots for a doctor on a specific date.
     *
     * Endpoint: GET /api/slots/{doctorCode}?date=YYYY-MM-DD
     *
     * Used for admin slot management view.
     *
     * @param doctorCode doctor's internal code
     * @param date       date to fetch slots for
     * @return List of SlotResponse DTOs
     */
    @GetMapping("/{doctorCode}")
    public ResponseEntity<List<SlotResponse>> getSlotsByDoctorAndDate(
            @PathVariable("doctorCode") final String doctorCode,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate date) {

        final var response = doctorSlotService.getSlotsByDoctorAndDate(doctorCode, date);
        return ResponseEntity.ok(response);
    }
}
