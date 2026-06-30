package org.codewithzea.doccasetracker.controller;

import lombok.RequiredArgsConstructor;
import org.codewithzea.doccasetracker.dto.response.ApiResponse;
import org.codewithzea.doccasetracker.dto.response.ApprovalStatusResponse;
import org.codewithzea.doccasetracker.dto.response.AuditLogResponse;
import org.codewithzea.doccasetracker.service.AdminService;
import org.codewithzea.doccasetracker.service.AuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final AuditLogService auditLogService;

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
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ApprovalStatusResponse> response = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success("Users successfully retrieved", response));
    }

    @GetMapping("/users/pending")
    public ResponseEntity<ApiResponse<Page<ApprovalStatusResponse>>> getPendingUsers(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ApprovalStatusResponse> response = adminService.getPendingUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success("Pending users successfully retrieved", response));
    }

    @GetMapping("/users/approved")
    public ResponseEntity<ApiResponse<Page<ApprovalStatusResponse>>> getApprovedUsers(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
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


    /********************         AUDIT LOGS FOR ADMIN       ***************************************/

    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> getLogs(
            @PageableDefault(sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable
    ) {

        Page<AuditLogResponse> response =
                auditLogService.getLogs(pageable);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Audit logs successfully retrieved",
                        response
                )
        );
    }

    @GetMapping("/audit-logs/recent")
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> getRecentLogs(
            @RequestParam(defaultValue = "10") int limit
    ) {

        Page<AuditLogResponse> response =
                auditLogService.getRecentLogs(limit);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Recent audit logs retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/audit-logs/user/{userId}")
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> getLogsByUserId(
            @PathVariable String userId,
            @PageableDefault(sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable
    ) {

        Page<AuditLogResponse> response =
                auditLogService.getLogsByUserId(userId, pageable);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "User audit logs retrieved successfully",
                        response
                )
        );
    }

}
