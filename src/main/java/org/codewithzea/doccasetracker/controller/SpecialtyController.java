package org.codewithzea.doccasetracker.controller;

import lombok.RequiredArgsConstructor;
import org.codewithzea.doccasetracker.dto.response.ApiResponse;
import org.codewithzea.doccasetracker.dto.response.SpecialtyResponse;
import org.codewithzea.doccasetracker.repository.SpecialtyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/specialties")
@RequiredArgsConstructor
public class SpecialtyController {

    private final SpecialtyRepository specialtyRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SpecialtyResponse>>> getAllSpecialties() {

        List<SpecialtyResponse> specialties =
                specialtyRepository.findAll()
                        .stream()
                        .map(s -> new SpecialtyResponse(
                                s.getId(),
                                s.getName()
                        ))
                        .toList();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Specialties retrieved successfully",
                        specialties
                )
        );
    }
}
