package org.codewithzea.doccasetracker.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codewithzea.doccasetracker.dto.request.CreateCaseRequest;
import org.codewithzea.doccasetracker.dto.request.UpdateCaseRequest;
import org.codewithzea.doccasetracker.dto.response.CaseResponse;
import org.codewithzea.doccasetracker.entity.*;
import org.codewithzea.doccasetracker.exception.ResourceNotFoundException;
import org.codewithzea.doccasetracker.mapper.CaseMapper;
import org.codewithzea.doccasetracker.repository.CaseRepository;
import org.codewithzea.doccasetracker.repository.DoctorRepository;
import org.codewithzea.doccasetracker.repository.TestRepository;
import org.codewithzea.doccasetracker.service.AuditLogService;
import org.codewithzea.doccasetracker.service.CaseService;
import org.codewithzea.doccasetracker.util.AuthenticatedUserProvider;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.codewithzea.doccasetracker.util.AuditActions.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CaseServiceImpl implements CaseService {

    private static final String CASE_CACHE = "case";
    private static final String CASES_CACHE = "cases";
    private static final String CASE_NOT_FOUND = "Case not Found";
    private static final String DOCTOR_NOT_FOUND = "Doctor not Found";

    private final CaseRepository caseRepository;
    private final DoctorRepository doctorRepository;
    private final CaseMapper caseMapper;
    private final AuditLogService auditLogService;
    private final AuthenticatedUserProvider userProvider;
    private final TestRepository testRepository;

    // ================= CREATE =================

    @Override
    @CacheEvict(value = {CASE_CACHE, CASES_CACHE}, allEntries = true)
    public CaseResponse createCase(CreateCaseRequest request) {

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException(DOCTOR_NOT_FOUND));

        List<Test> tests = getTests(request.getTestIds());

        Cases cases = Cases.builder()
                .doctor(doctor)
                .patientName(request.getPatientName())
                .deleted(false)
                .build();

        cases.setCaseTests(buildCaseTests(cases, tests));

        Cases saved = caseRepository.save(cases);

        User admin = userProvider.getCurrentUser();

        auditLogService.log(
                CASE_CREATED,
                "CASE",
                saved.getId(),
                "Created case for patient " + saved.getPatientName(),
                admin.getEmail(),
                admin.getId(),
                admin.getRole().getName().name()
        );

        log.info("Case created: {}", saved.getId());

        return caseMapper.toResponse(saved);
    }

    // ================= GET BY ID =================

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CASE_CACHE, key = "#caseId")
    public CaseResponse getCaseById(String caseId) {

        Cases cases = caseRepository.findByIdAndDeletedFalse(caseId)
                .orElseThrow(() -> new ResourceNotFoundException(CASE_NOT_FOUND));

        return caseMapper.toResponse(cases);
    }

    // ================= GET ALL =================

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CASES_CACHE, key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<CaseResponse> getAllCases(Pageable pageable) {

        return caseRepository.findAllByDeletedFalse(pageable)
                .map(caseMapper::toResponse);
    }

    // ================= UPDATE =================

    @Override
    @CacheEvict(value = {CASE_CACHE, CASES_CACHE}, allEntries = true)
    public CaseResponse updateCase(String id, UpdateCaseRequest request) {

        Cases cases = caseRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(CASE_NOT_FOUND));

        if (request.getDoctorId() != null) {
            Doctor doctor = doctorRepository.findById(request.getDoctorId())
                    .orElseThrow(() -> new ResourceNotFoundException(DOCTOR_NOT_FOUND));

            cases.setDoctor(doctor);
        }

        if (request.getPatientName() != null) {
            cases.setPatientName(request.getPatientName());
        }


        if (request.getTestIds() != null && !request.getTestIds().isEmpty()) {

            List<Test> tests = getTests(request.getTestIds());

            cases.getCaseTests().clear();
            cases.getCaseTests().addAll(buildCaseTests(cases, tests));
        }

        Cases updated = caseRepository.save(cases);

        User admin = userProvider.getCurrentUser();

        auditLogService.log(
                CASE_UPDATED,
                "CASE",
                updated.getId(),
                "Updated case for patient " + updated.getPatientName(),
                admin.getEmail(),
                admin.getId(),
                admin.getRole().getName().name()
        );

        log.info("Case updated: {}", updated.getId());

        return caseMapper.toResponse(updated);
    }

    // ================= DELETE (SOFT) =================

    @Override
    @CacheEvict(value = {CASE_CACHE, CASES_CACHE}, allEntries = true)
    public void deleteCase(String id) {

        Cases cases = caseRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(CASE_NOT_FOUND));

        cases.setDeleted(true);
        cases.setDeletedAt(LocalDateTime.now());

        caseRepository.save(cases);

        User admin = userProvider.getCurrentUser();

        auditLogService.log(
                CASE_DELETED,
                "CASE",
                cases.getId(),
                "Deleted case for patient " + cases.getPatientName(),
                admin.getEmail(),
                admin.getId(),
                admin.getRole().getName().name()
        );

        log.info("Case soft deleted: {}", id);
    }


    private List<Test> getTests(List<String> testIds) {

        List<Test> tests = testRepository.findByIdInAndActiveTrue(testIds);

        if (tests.size() != testIds.size()) {
            throw new ResourceNotFoundException("One or more tests not found");
        }

        List<String> inactiveTests = tests.stream()
                .filter(t -> !t.isActive())
                .map(Test::getTestName)
                .toList();

        if (!inactiveTests.isEmpty()) {
            throw new IllegalStateException(
                    "These tests are inactive and cannot be used: " + inactiveTests
            );
        }

        return tests;
    }

    private List<CaseTest> buildCaseTests(Cases cases, List<Test> tests) {

        return tests.stream()
                .map(test -> CaseTest.builder()
                        .cases(cases)
                        .test(test)
                        .testNameAtTime(test.getTestName())
                        .priceAtTime(test.getPrice())
                        .commissionAtTime(test.getCommission())
                        .build())
                .toList();
    }
}