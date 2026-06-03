package org.codewithzea.doccasetracker.controller;

import lombok.RequiredArgsConstructor;
import org.codewithzea.doccasetracker.dto.response.ApiResponse;
import org.codewithzea.doccasetracker.dto.response.DashboardAnalyticsResponse;
import org.codewithzea.doccasetracker.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
public class DashboardAnalyticsController {

    private final DashboardService dashboardService;

    @GetMapping("/analytics")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<DashboardAnalyticsResponse>> getAnalytics() {
        DashboardAnalyticsResponse response = dashboardService.getDashboardAnalytics();
        return ResponseEntity.ok(
              ApiResponse.success("Admin Analytics retrieved successfully", response)
        );
    }
}
