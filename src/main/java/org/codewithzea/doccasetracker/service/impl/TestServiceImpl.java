package org.codewithzea.doccasetracker.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codewithzea.doccasetracker.dto.request.CreateTestRequest;
import org.codewithzea.doccasetracker.dto.request.UpdateTestRequest;
import org.codewithzea.doccasetracker.dto.response.TestResponse;
import org.codewithzea.doccasetracker.entity.Test;
import org.codewithzea.doccasetracker.entity.User;
import org.codewithzea.doccasetracker.exception.ResourceNotFoundException;
import org.codewithzea.doccasetracker.mapper.TestMapper;
import org.codewithzea.doccasetracker.repository.CaseTestRepository;
import org.codewithzea.doccasetracker.repository.TestRepository;
import org.codewithzea.doccasetracker.service.AuditLogService;
import org.codewithzea.doccasetracker.service.TestService;
import org.codewithzea.doccasetracker.util.AuditActions;
import org.codewithzea.doccasetracker.util.AuthenticatedUserProvider;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TestServiceImpl implements TestService {
    private final AuthenticatedUserProvider userProvider;
    private final AuditLogService auditLogService;
    private final CaseTestRepository caseTestRepository;

    public static final String TEST_CACHE = "test";
    public static final String TESTS_CACHE = "tests";

    private final TestRepository repository;
    private final TestMapper mapper;

    @Override
    @CacheEvict(value = {TEST_CACHE, TESTS_CACHE}, allEntries = true)
    public TestResponse create(CreateTestRequest dto) {

        log.debug("Creating test: {}", dto.getTestName());

        if (repository.existsByTestName(dto.getTestName())) {
            throw new IllegalArgumentException("Test already exists");
        }




        Test test = mapper.toEntity(dto);
        test.setActive(true);

        log.info("Mapped test: {}", test);

        Test saved = repository.save(test);

        User admin = userProvider.getCurrentUser();

        auditLogService.log(
                AuditActions.TEST_CREATED,
                "TEST",
                saved.getId(),
                "Test created: " + saved.getTestName(),
                admin.getEmail(),
                admin.getId(),
                admin.getRole().getName().name()
        );

        log.info("Test created successfully: {}", saved.getId());

        return mapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = TESTS_CACHE)
    public List<TestResponse> getAll() {

        log.debug("Fetching all active tests");

        return repository.findByActiveTrue()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = TEST_CACHE, key = "#id")
    public TestResponse getById(String id) {

        log.debug("Fetching test {}", id);

        Test test = findTestOrThrow(id);

        return mapper.toDTO(test);
    }

    @Override
    @CacheEvict(value = {TEST_CACHE, TESTS_CACHE}, allEntries = true)
    public TestResponse update(String id, UpdateTestRequest dto) {

        log.debug("Updating test {}", id);

        Test test = findTestOrThrow(id);

        mapper.updateEntityFromDto(dto, test);

        Test updated = repository.save(test);

        User admin = userProvider.getCurrentUser();

        auditLogService.log(
                AuditActions.TEST_UPDATED,
                "TEST",
                id,
                "Test updated: " + updated.getTestName(),
                admin.getEmail(),
                admin.getId(),
                admin.getRole().getName().name()
        );

        log.info("Test updated successfully: {}", id);

        return mapper.toDTO(updated);
    }



    @Override
    @CacheEvict(value = {TEST_CACHE, TESTS_CACHE}, allEntries = true)
    public void delete(String id) {

        log.debug("Deleting test {}", id);

        Test test = findTestOrThrow(id);

        if (caseTestRepository.existsByTest_Id(id)) {
            throw new IllegalStateException(
                    "This test has already been used in one or more cases. Please deactivate it instead."
            );
        }

        repository.delete(test);

        User admin = userProvider.getCurrentUser();

        auditLogService.log(
                AuditActions.TEST_DELETED,
                "TEST",
                id,
                "Test deleted: " + test.getTestName(),
                admin.getEmail(),
                admin.getId(),
                admin.getRole().getName().name()
        );

        log.info("Test deleted successfully: {}", id);
    }


    @Transactional(readOnly = true)
    @Override
    public Page<TestResponse> getInactiveTests(Pageable pageable) {
        return repository.findByActiveFalse(pageable)
                .map(mapper::toDTO);
    }

    @Override
    @CacheEvict(value = {TEST_CACHE, TESTS_CACHE}, allEntries = true)
    public TestResponse activate(String id) {

        Test test = findTestOrThrow(id);

        test.setActive(true);

        Test saved = repository.save(test);

        User admin = userProvider.getCurrentUser();

        auditLogService.log(
                AuditActions.TEST_ACTIVATED,
                "TEST",
                id,
                "Test activated: " + saved.getTestName(),
                admin.getEmail(),
                admin.getId(),
                admin.getRole().getName().name()
        );

        log.info("Test activated {}", id);

        return mapper.toDTO(saved);
    }

    @Override
    @CacheEvict(value = {TEST_CACHE, TESTS_CACHE}, allEntries = true)
    public TestResponse deactivate(String id) {

        Test test = findTestOrThrow(id);

        test.setActive(false);

        Test saved = repository.save(test);

        User admin = userProvider.getCurrentUser();

        auditLogService.log(
                AuditActions.TEST_DEACTIVATED,
                "TEST",
                id,
                "Test deactivated: " + saved.getTestName(),
                admin.getEmail(),
                admin.getId(),
                admin.getRole().getName().name()
        );

        log.info("Test deactivated {}", id);

        return mapper.toDTO(saved);
    }

    private Test findTestOrThrow(String id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Test not found with id: " + id
                        ));
    }
}