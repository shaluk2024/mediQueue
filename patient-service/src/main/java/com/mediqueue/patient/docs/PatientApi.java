package com.mediqueue.patient.docs;

import com.mediqueue.patient.data.patient.PatientResponse;
import com.mediqueue.patient.data.patient.UpdatePatientRequest;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Patients", description = "APIs for managing patient profiles")
public interface PatientApi {

    // =========================================================
    // GET MY PROFILE
    // =========================================================

    @Operation(summary = "Get my patient profile")
    @ApiResponse(
            responseCode = "200",
            description = "Patient profile fetched successfully",
            content = @Content(
                    schema = @Schema(implementation = PatientResponse.class),
                    examples = @ExampleObject(
                            value = """
                            {
                              "patientId": "PAT-101-201",
                              "userId": "USR-101-201",
                              "name": "Rahul Verma",
                              "email": "rahul@gmail.com",
                              "phone": "9876543210",
                              "dateOfBirth": "1998-05-20",
                              "gender": "MALE",
                              "bloodGroup": "O+",
                              "medicalHistory": "Diabetes"
                            }
                            """
                    )
            )
    )
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<PatientResponse> getMyProfile(
            @Parameter(hidden = true)
            @RequestHeader("X-User-Id") String userCode
    );

    // =========================================================
    // GET PATIENT BY CODE
    // =========================================================

    @Operation(summary = "Get patient by code")
    @ApiResponse(
            responseCode = "200",
            description = "Patient fetched successfully",
            content = @Content(
                    schema = @Schema(implementation = PatientResponse.class),
                    examples = @ExampleObject(
                            value = """
                            {
                              "patientId": "PAT-101-201",
                              "userId": "USR-101-201",
                              "name": "Rahul Verma",
                              "email": "rahul@gmail.com",
                              "phone": "9876543210",
                              "dateOfBirth": "1998-05-20",
                              "gender": "MALE",
                              "bloodGroup": "O+",
                              "medicalHistory": "Diabetes"
                            }
                            """
                    )
            )
    )
    ResponseEntity<PatientResponse> getPatientByCode(
            @PathVariable("patientCode")
            @Parameter(example = "PAT-101-201")
            String patientCode
    );

    // =========================================================
    // UPDATE PROFILE
    // =========================================================

    @Operation(summary = "Update my patient profile")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Patient profile updated successfully",
                    content = @Content(
                            schema = @Schema(implementation = PatientResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "patientId": "PAT-101-201",
                                      "name": "Rahul Verma",
                                      "bloodGroup": "A+",
                                      "medicalHistory": "Hypertension"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<PatientResponse> updateMyProfile(

            @Parameter(hidden = true)
            @RequestHeader("X-User-Id") String userCode,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Update patient profile request",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UpdatePatientRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "dateOfBirth": "1995-08-15",
                                      "gender": "MALE",
                                      "bloodGroup": "A+",
                                      "medicalHistory": "Hypertension"
                                    }
                                    """
                            )
                    )
            )
            @RequestBody UpdatePatientRequest request
    );
}