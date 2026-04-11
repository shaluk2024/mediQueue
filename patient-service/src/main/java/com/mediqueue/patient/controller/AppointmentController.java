package com.mediqueue.patient.controller;

import com.mediqueue.patient.data.appointment.AppointmentResponse;
import com.mediqueue.patient.data.appointment.BookRequest;
import com.mediqueue.patient.data.appointment.SlotResponse;
import com.mediqueue.patient.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for managing appointment-related operations.
 *
 * Exposes APIs for:
 * - Viewing available slots
 * - Booking appointments
 * - Cancelling appointments
 * - Viewing patient appointments
 * - Viewing doctor queue
 *
 * Base path: /api/appointments
 */
@RestController // Marks this as REST controller
@RequestMapping("/api/appointments") // Base URL
@RequiredArgsConstructor // Lombok: constructor injection
public class AppointmentController {

    /**
     * Service layer handling appointment business logic.
     */
    private final AppointmentService appointmentService;

    /**
     * Get available slots for a doctor on a given date.
     *
     * Endpoint: GET /api/appointments/slots/{doctorId}?date=YYYY-MM-DD
     *
     * @param doctorId ID of the doctor
     * @param date     Date for which slots are required
     * @return List of available slots
     */
    @GetMapping("/slots/{doctorId}")
    public ResponseEntity<List<SlotResponse>> getAvailableSlots(
            @PathVariable("doctorId") final String doctorId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate date) {

        final var response = appointmentService.getAvailableSlots(doctorId, date);
        return ResponseEntity.ok(response);
    }

    /**
     * Book an appointment.
     *
     * Endpoint: POST /api/appointments/book
     *
     * Requires:
     * - Request body (slotId, doctorId, symptoms)
     * - Header: X-User-Id (injected by API Gateway)
     * - Query param: slotDate
     *
     * @param request   booking request DTO
     * @param patientId ID of logged-in patient (from header)
     * @param slotDate  date of the selected slot
     * @return Appointment confirmation response
     */
    @PostMapping("/book")
    public ResponseEntity<AppointmentResponse> bookAppointment(
            @Valid @RequestBody final BookRequest request,
            @RequestHeader("X-User-Id") final String patientId,
            @RequestParam("slotDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate slotDate) {

        final var response = appointmentService.bookAppointment(request, patientId, slotDate);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Cancel an appointment.
     *
     * Endpoint: DELETE /api/appointments/{appointmentId}
     *
     * Only the patient who booked the appointment can cancel it.
     *
     * @param appointmentId ID of the appointment
     * @param patientId     ID of logged-in patient (from header)
     * @return HTTP 204 No Content on success
     */
    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<Void> cancelAppointment(
            @PathVariable("appointmentId") final String appointmentId,
            @RequestHeader("X-User-Id") final String patientId) {

        appointmentService.cancelAppointment(appointmentId, patientId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Get all appointments of the logged-in patient.
     *
     * Endpoint: GET /api/appointments/my
     *
     * @param patientId ID of logged-in patient (from header)
     * @return List of patient's appointments (latest first)
     */
    @GetMapping("/my")
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments(
            @RequestHeader("X-User-Id") final String patientId) {

        final var response = appointmentService.getMyAppointments(patientId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get doctor's queue for a given date.
     *
     * Endpoint: GET /api/appointments/queue/{doctorId}?date=YYYY-MM-DD
     *
     * If date is not provided, defaults to today's date.
     *
     * @param doctorId ID of the doctor
     * @param date     Optional date (defaults to today)
     * @return Ordered list representing doctor's queue
     */
    @GetMapping("/queue/{doctorId}")
    public ResponseEntity<List<AppointmentResponse>> getDoctorQueue(
            @PathVariable("doctorId") final String doctorId,
            @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        // Default to today's date if not provided
        if (date == null) date = LocalDate.now();

        final var response = appointmentService.getDoctorQueue(doctorId, date);
        return ResponseEntity.ok(response);
    }
}