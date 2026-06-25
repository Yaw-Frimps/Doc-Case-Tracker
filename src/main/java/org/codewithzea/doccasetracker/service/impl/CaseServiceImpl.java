package org.codewithzea.doccasetracker.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codewithzea.doccasetracker.dto.request.CreateCaseRequest;
import org.codewithzea.doccasetracker.dto.request.UpdateCaseRequest;
import org.codewithzea.doccasetracker.dto.response.CaseResponse;
import org.codewithzea.doccasetracker.entity.Cases;
import org.codewithzea.doccasetracker.entity.Doctor;
import org.codewithzea.doccasetracker.entity.Test;
import org.codewithzea.doccasetracker.entity.User;
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

        Test test = testRepository.findById(request.getTestId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Test not found"));

        Cases cases = Cases.builder()
                .doctor(doctor)
                .patientName(request.getPatientName())
                .numberOfCases(request.getNumberOfCases())
                .test(test)
                .deleted(false)
                .build();

        Cases saved = caseRepository.save(cases);

        // 👇 AUDIT LOG
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

        if (request.getNumberOfCases() != null) {
            cases.setNumberOfCases(request.getNumberOfCases());
        }

        if (request.getTestId() != null) {

            Test test = testRepository.findById(request.getTestId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Test not found"));

            cases.setTest(test);
        }

        Cases updated = caseRepository.save(cases);

        // 👇 AUDIT LOG
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

        // 👇 AUDIT LOG
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
}