package com.mediqueue.patient.docs;

import com.mediqueue.patient.data.doctor.DoctorResponse;
import com.mediqueue.patient.data.doctor.UpdateDoctorRequest;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Doctors", description = "APIs for managing doctor profiles and search")
public interface DoctorApi {

    // =========================================================
    // GET MY PROFILE
    // =========================================================

    @Operation(summary = "Get my doctor profile")
    @ApiResponse(
            responseCode = "200",
            description = "Doctor profile fetched successfully",
            content = @Content(
                    schema = @Schema(implementation = DoctorResponse.class),
                    examples = @ExampleObject(
                            value = """
                            {
                              "doctorId": "DOC-201-301",
                              "name": "Dr. Amit Sharma",
                              "email": "amit.sharma@gmail.com",
                              "phone": "9876543210",
                              "specialization": "Cardiology",
                              "qualification": "MBBS, MD",
                              "experienceYears": 10,
                              "consultationFee": 500,
                              "available": true
                            }
                            """
                    )
            )
    )
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<DoctorResponse> getMyProfile(
            @Parameter(hidden = true)
            @RequestHeader("X-User-Id") String userCode
    );

    // =========================================================
    // GET DOCTOR BY CODE
    // =========================================================

    @Operation(summary = "Get doctor by code")
    @ApiResponse(
            responseCode = "200",
            description = "Doctor fetched successfully",
            content = @Content(
                    schema = @Schema(implementation = DoctorResponse.class),
                    examples = @ExampleObject(
                            value = """
                            {
                              "doctorId": "DOC-201-301",
                              "name": "Dr. Amit Sharma",
                              "email": "amit.sharma@gmail.com",
                              "phone": "9876543210",
                              "specialization": "Cardiology",
                              "qualification": "MBBS, MD",
                              "experienceYears": 10,
                              "consultationFee": 500,
                              "available": true
                            }
                            """
                    )
            )
    )
    ResponseEntity<DoctorResponse> getDoctorByCode(
            @PathVariable("doctorCode")
            @Parameter(example = "DOC-201-301")
            String doctorCode
    );

    // =========================================================
    // GET ALL DOCTORS
    // =========================================================

    @Operation(summary = "Get all available doctors")
    @ApiResponse(
            responseCode = "200",
            description = "Doctors fetched successfully",
            content = @Content(
                    schema = @Schema(implementation = DoctorResponse.class),
                    examples = @ExampleObject(
                            value = """
                            [
                              {
                                "doctorId": "DOC-201-301",
                                "name": "Dr. Amit Sharma",
                                "specialization": "Cardiology",
                                "experienceYears": 10,
                                "consultationFee": 500,
                                "available": true
                              }
                            ]
                            """
                    )
            )
    )
    ResponseEntity<List<DoctorResponse>> getAllAvailableDoctors();

    // =========================================================
    // SEARCH DOCTOR
    // =========================================================

    @Operation(summary = "Search doctors by specialization")
    @ApiResponse(
            responseCode = "200",
            description = "Doctors fetched successfully",
            content = @Content(
                    schema = @Schema(implementation = DoctorResponse.class),
                    examples = @ExampleObject(
                            value = """
                            [
                              {
                                "doctorId": "DOC-201-301",
                                "name": "Dr. Amit Sharma",
                                "specialization": "Cardiology",
                                "experienceYears": 10,
                                "consultationFee": 500,
                                "available": true
                              }
                            ]
                            """
                    )
            )
    )
    ResponseEntity<List<DoctorResponse>> searchBySpecialization(
            @RequestParam("specialization")
            @Parameter(example = "Cardiology")
            String specialization
    );

    // =========================================================
    // UPDATE PROFILE
    // =========================================================

    @Operation(summary = "Update my doctor profile")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Doctor profile updated successfully",
                    content = @Content(
                            schema = @Schema(implementation = DoctorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "doctorId": "DOC-201-301",
                                      "name": "Dr. Amit Sharma",
                                      "specialization": "Dermatology",
                                      "qualification": "MBBS, MD",
                                      "experienceYears": 12,
                                      "consultationFee": 700,
                                      "available": true
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<DoctorResponse> updateMyProfile(

            @Parameter(hidden = true)
            @RequestHeader("X-User-Id") String userCode,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Update doctor profile request",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UpdateDoctorRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "specialization": "Dermatology",
                                      "experienceYears": 12,
                                      "consultationFee": 700,
                                      "available": true
                                    }
                                    """
                            )
                    )
            )
            @RequestBody UpdateDoctorRequest request
    );
}