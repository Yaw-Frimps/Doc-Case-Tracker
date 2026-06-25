package org.codewithzea.doccasetracker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codewithzea.doccasetracker.dto.request.CreateCaseRequest;
import org.codewithzea.doccasetracker.dto.request.UpdateCaseRequest;
import org.codewithzea.doccasetracker.dto.response.ApiResponse;
import org.codewithzea.doccasetracker.dto.response.CaseResponse;
import org.codewithzea.doccasetracker.service.CaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cases")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('ROLE_WORKER')")
public class CaseController {

    private final CaseService caseService;

    // ================= CREATE CASE =================
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CaseResponse>> createCase(
            @Valid @RequestBody CreateCaseRequest request) {

        CaseResponse response = caseService.createCase(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Case created successfully",
                        response
                )
        );
    }

    // ================= GET CASE BY ID =================
    @GetMapping("/{caseId}")
    public ResponseEntity<ApiResponse<CaseResponse>> getCaseById(
            @PathVariable String caseId) {

        CaseResponse response = caseService.getCaseById(caseId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Case retrieved successfully",
                        response
                )
        );
    }

    // ================= GET ALL CASES =================
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CaseResponse>>> getAllCases(
            Pageable pageable) {

        Page<CaseResponse> response = caseService.getAllCases(pageable);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cases retrieved successfully",
                        response
                )
        );
    }

    // ================= UPDATE CASE =================
    @PatchMapping("/{caseId}/update")
    public ResponseEntity<ApiResponse<CaseResponse>> updateCase(
            @PathVariable String caseId,
            @RequestBody UpdateCaseRequest request) {

        CaseResponse response = caseService.updateCase(caseId, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Case updated successfully",
                        response
                )
        );
    }

    // ================= DELETE CASE (SOFT DELETE) =================
    @DeleteMapping("/{caseId}/delete")
    public ResponseEntity<ApiResponse<Void>> deleteCase(
            @PathVariable String caseId) {

        caseService.deleteCase(caseId);

        return ResponseEntity.ok(
                ApiResponse.success("Case deleted successfully")
        );
    }
}