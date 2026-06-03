package org.codewithzea.doccasetracker.controller;

import lombok.RequiredArgsConstructor;
import org.codewithzea.doccasetracker.dto.response.ApiResponse;
import org.codewithzea.doccasetracker.dto.response.ApprovalStatusResponse;
import org.codewithzea.doccasetracker.service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @PutMapping("/users/{id}/approve")
    public ResponseEntity<ApiResponse<ApprovalStatusResponse>> approveUser(
            @PathVariable String id) {
        ApprovalStatusResponse response = adminService.approveUser(id);
        return ResponseEntity.ok(ApiResponse.success("User Approved Successfully", response));
    }

    @PutMapping("/users/{id}/reject")
    public ResponseEntity<ApiResponse<ApprovalStatusResponse>> rejectUser(
            @PathVariable String id) {
        ApprovalStatusResponse response = adminService.rejectUser(id);
        return ResponseEntity.ok(ApiResponse.success("User Rejected Successfully", response));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<ApprovalStatusResponse>>> getAllUsers(
            Pageable pageable) {
        Page<ApprovalStatusResponse> response = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success("Users successfully retrieved", response));
    }

    @GetMapping("/users/pending")
    public ResponseEntity<ApiResponse<Page<ApprovalStatusResponse>>> getPendingUsers(
            Pageable pageable) {
        Page<ApprovalStatusResponse> response = adminService.getPendingUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success("Pending users successfully retrieved", response));
    }

    @GetMapping("/users/approved")
    public ResponseEntity<ApiResponse<Page<ApprovalStatusResponse>>> getApprovedUsers(
            Pageable pageable) {
        Page<ApprovalStatusResponse> response = adminService.getApprovedUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success("Approved users successfully retrieved", response));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<ApprovalStatusResponse>> getUserById(
            @PathVariable String id) {
        ApprovalStatusResponse response = adminService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("User successfully retrieved", response));
    }

    @DeleteMapping("/users/{id}/delete")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable String id) {

        adminService.deleteUser(id);

        return ResponseEntity.ok(ApiResponse.success("User successfully deleted"));
    }
}
