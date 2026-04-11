package com.mediqueue.patient.docs;

import com.mediqueue.patient.data.appointment.AppointmentResponse;
import com.mediqueue.patient.data.appointment.BookRequest;
import com.mediqueue.patient.data.appointment.SlotResponse;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Appointments", description = "APIs for managing slots, booking, cancellation and doctor queue")
public interface AppointmentApi {

    @Operation(summary = "Get available slots for a doctor")
    @ApiResponse(
            responseCode = "200",
            description = "Slots fetched successfully",
            content = @Content(
                    schema = @Schema(implementation = SlotResponse.class),
                    examples = @ExampleObject(
                            value = """
                            [
                              {
                                "id": "SLT-101-201",
                                "slotDate": "2026-04-11",
                                "startTime": "10:00:00",
                                "endTime": "10:15:00",
                                "status": "AVAILABLE"
                              }
                            ]
                            """
                    )
            )
    )
    ResponseEntity<List<SlotResponse>> getAvailableSlots(
            @Parameter(example = "DOC-201-301") String doctorId,
            @Parameter(example = "2026-04-11") LocalDate date
    );

    // ------------------------------------------------------

    @Operation(summary = "Book an appointment")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Appointment booked successfully",
                    content = @Content(
                            schema = @Schema(implementation = AppointmentResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "appointmentId": "APT-101-201",
                                      "patientName": "Rahul Verma",
                                      "doctorId": "DOC-201-301",
                                      "doctorName": "Dr. Amit Sharma",
                                      "specialization": "Cardiology",
                                      "slotDate": "2026-04-11",
                                      "startTime": "10:00:00",
                                      "endTime": "10:15:00",
                                      "status": "CONFIRMED",
                                      "triagePriority": "HIGH",
                                      "triageReasoning": "Symptoms indicate urgency",
                                      "queuePosition": 2
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "409", description = "Slot already booked")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<AppointmentResponse> bookAppointment(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Booking request",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = BookRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "slotId": "SLT-101-201",
                                      "doctorId": "DOC-201-301",
                                      "symptoms": "Fever and headache"
                                    }
                                    """
                            )
                    )
            )
            @RequestBody BookRequest request,

            @Parameter(hidden = true)
            @RequestHeader("X-User-Id") String patientId,

            @Parameter(example = "2026-04-11")
            LocalDate slotDate
    );

    // ------------------------------------------------------

    @Operation(summary = "Cancel an appointment")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Appointment cancelled"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<Void> cancelAppointment(
            @Parameter(example = "APT-101-201") String appointmentId,
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String patientId
    );

    // ------------------------------------------------------

    @Operation(summary = "Get my appointments")
    @ApiResponse(
            responseCode = "200",
            description = "Appointments fetched successfully",
            content = @Content(
                    schema = @Schema(implementation = AppointmentResponse.class),
                    examples = @ExampleObject(
                            value = """
                            [
                              {
                                "appointmentId": "APT-101-201",
                                "patientName": "Rahul Verma",
                                "doctorName": "Dr. Amit Sharma",
                                "status": "CONFIRMED",
                                "queuePosition": 1
                              }
                            ]
                            """
                    )
            )
    )
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<List<AppointmentResponse>> getMyAppointments(
            @Parameter(hidden = true)
            @RequestHeader("X-User-Id") String patientId
    );

    // ------------------------------------------------------

    @Operation(summary = "Get doctor's queue")
    @ApiResponse(
            responseCode = "200",
            description = "Queue fetched successfully",
            content = @Content(
                    schema = @Schema(implementation = AppointmentResponse.class),
                    examples = @ExampleObject(
                            value = """
                            [
                              {
                                "appointmentId": "APT-101-201",
                                "patientName": "Rahul Verma",
                                "triagePriority": "HIGH",
                                "queuePosition": 1,
                                "startTime": "10:00:00"
                              }
                            ]
                            """
                    )
            )
    )
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<List<AppointmentResponse>> getDoctorQueue(
            @Parameter(example = "DOC-201-301") String doctorId,
            @Parameter(example = "2026-04-11") LocalDate date
    );
}