package org.codewithzea.doccasetracker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codewithzea.doccasetracker.dto.request.CreateDoctorRequest;
import org.codewithzea.doccasetracker.dto.request.UpdateDoctorRequest;
import org.codewithzea.doccasetracker.dto.response.ApiResponse;
import org.codewithzea.doccasetracker.dto.response.DoctorResponse;
import org.codewithzea.doccasetracker.service.DoctorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('ROLE_WORKER')")
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<DoctorResponse>> createDoctor(
            @Valid @RequestBody CreateDoctorRequest request) {
        DoctorResponse response = doctorService.createDoctor(request);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Doctor created successfully",
                        response
                )
        );
    }

    @GetMapping("/{doctorId}")
    public ResponseEntity<ApiResponse<DoctorResponse>> getDoctor(
            @PathVariable String doctorId) {
        DoctorResponse response = doctorService.getDoctor(doctorId);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Doctor retrieved successfully",
                        response
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<DoctorResponse>>> getAllDoctors(
            Pageable pageable) {
        Page<DoctorResponse> response = doctorService.getAllDoctors(pageable);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Doctors retrieved successfully",
                        response
                )
        );
    }

    @PutMapping("/{doctorId}")
    public ResponseEntity<ApiResponse<DoctorResponse>> updateDoctor(
            @PathVariable String doctorId,
            @RequestBody UpdateDoctorRequest request) {
        DoctorResponse response = doctorService.updateDoctor(doctorId, request);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Doctor updated successfully",
                        response
                )
        );
    }

    @PatchMapping("/{doctorId}/activate")
    public ResponseEntity<ApiResponse<DoctorResponse>> activateDoctor(@PathVariable String doctorId) {
        DoctorResponse response = doctorService.activateDoctor(doctorId);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Doctor activated successfully",
                        response
                )
        );

    }

    @PatchMapping("/{doctorId}/deactivate")
    public ResponseEntity<ApiResponse<DoctorResponse>> deactivateDoctor(@PathVariable String doctorId) {
        DoctorResponse response = doctorService.deactivateDoctor(doctorId);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Doctor deactivated successfully",
                        response
                )
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<DoctorResponse>>> searchDoctors(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String search,
            Pageable pageable
    ) {

        String query = keyword != null ? keyword : search;

        Page<DoctorResponse> response =
                doctorService.searchDoctors(query, pageable);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Doctors retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/specialty/{specialtyId}")
    public ResponseEntity<ApiResponse<Page<DoctorResponse>>> getDoctorsBySpecialty(
            @PathVariable String specialtyId,
            Pageable pageable
    ) {
        Page<DoctorResponse> response =
                doctorService.getDoctorsBySpecialty(specialtyId, pageable);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Doctors retrieved successfully with specialty: ",
                        response
                )
        );
    }


    @DeleteMapping("/{doctorId}")
    public ResponseEntity<ApiResponse<Void>> deleteDoctor(
            @PathVariable String doctorId) {

        doctorService.deleteDoctor(doctorId);

        return ResponseEntity.ok(
                ApiResponse.success("Doctor deleted successfully")
        );
    }

    @GetMapping("/deleted")
    public ResponseEntity<ApiResponse<Page<DoctorResponse>>> getDeletedDoctors(
            Pageable pageable
    ) {
        Page<DoctorResponse> response =
                doctorService.getDeletedDoctors(pageable);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Deleted doctors retrieved successfully",
                        response
                )
        );
    }

    @PatchMapping("/{doctorId}/restore")
    public ResponseEntity<ApiResponse<DoctorResponse>> restoreDoctor(
            @PathVariable String doctorId
    ) {
        DoctorResponse response = doctorService.restoreDoctor(doctorId);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Doctor restored successfully",
                        response
                )
        );
    }
}
