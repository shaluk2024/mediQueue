package com.mediqueue.patient.docs;

import com.mediqueue.patient.data.appointment.CreateSlotRequest;
import com.mediqueue.patient.data.appointment.SlotResponse;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Doctor Slots", description = "APIs for managing doctor slots")
public interface DoctorSlotApi {

    // =========================================================
    // CREATE SLOT
    // =========================================================

    @Operation(summary = "Create a doctor slot")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Slot created successfully",
                    content = @Content(
                            schema = @Schema(implementation = SlotResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "id": "SLT-101-201",
                                      "slotDate": "2026-04-11",
                                      "startTime": "10:00:00",
                                      "endTime": "10:15:00",
                                      "status": "AVAILABLE"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "403", description = "Forbidden (Admin only)")
    })
    ResponseEntity<SlotResponse> createSlot(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Create slot request",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateSlotRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "doctorId": "DOC-201-301",
                                      "slotDate": "2026-04-11",
                                      "startTime": "10:00:00",
                                      "endTime": "10:15:00"
                                    }
                                    """
                            )
                    )
            )
            @RequestBody CreateSlotRequest request
    );

    // =========================================================
    // DELETE SLOT
    // =========================================================

    @Operation(summary = "Delete a slot")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Slot deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Slot cannot be deleted (not AVAILABLE)"),
            @ApiResponse(responseCode = "403", description = "Forbidden (Admin only)"),
            @ApiResponse(responseCode = "404", description = "Slot not found")
    })
    ResponseEntity<Void> deleteSlot(
            @PathVariable("slotCode")
            @Parameter(example = "SLT-101-201")
            String slotCode
    );

    // =========================================================
    // GET SLOTS BY DOCTOR + DATE
    // =========================================================

    @Operation(summary = "Get slots by doctor and date")
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
    ResponseEntity<List<SlotResponse>> getSlotsByDoctorAndDate(
            @PathVariable("doctorCode")
            @Parameter(example = "DOC-201-301")
            String doctorCode,

            @RequestParam("date")
            @Parameter(example = "2026-04-11")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    );
}