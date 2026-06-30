package org.codewithzea.doccasetracker.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codewithzea.doccasetracker.dto.request.CreateTestRequest;
import org.codewithzea.doccasetracker.dto.request.UpdateTestRequest;
import org.codewithzea.doccasetracker.dto.response.ApiResponse;
import org.codewithzea.doccasetracker.dto.response.TestResponse;
import org.codewithzea.doccasetracker.service.TestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tests")
@RequiredArgsConstructor
@Slf4j

public class TestController {

    private final TestService service;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<TestResponse>> create(
            @Valid @RequestBody CreateTestRequest request
    ) {
        TestResponse response = service.create(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Test created successfully",
                        response
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_WORKER')")
    public ResponseEntity<ApiResponse<TestResponse>> getById(
            @PathVariable String id
    ) {
        TestResponse response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Test retrieved successfully",
                        response
                )
        );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_WORKER')")
    public ResponseEntity<ApiResponse<List<TestResponse>>> getAll() {

        List<TestResponse> response = service.getAll();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Tests retrieved successfully",
                        response
                )
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<TestResponse>> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateTestRequest request
    ) {
        TestResponse response = service.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Test updated successfully",
                        response
                )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable String id
    ) {
        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.success("Test deleted successfully")
        );
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<TestResponse>> activate(
            @PathVariable String id
    ) {
        TestResponse response = service.activate(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Test activated successfully",
                        response
                )
        );
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<TestResponse>> deactivate(
            @PathVariable String id
    ) {
        TestResponse response = service.deactivate(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Test deactivated successfully",
                        response
                )
        );
    }

    @GetMapping("/inactive")
    public ResponseEntity<ApiResponse<Page<TestResponse>>> getInactiveTests(
            Pageable pageable
    ) {

        Page<TestResponse> response = service.getInactiveTests(pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Inactive tests retrieved successfully", response)
        );
    }
}