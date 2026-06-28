package org.codewithzea.doccasetracker.service.impl;

import lombok.RequiredArgsConstructor;
import org.codewithzea.doccasetracker.dto.response.ApprovalStatusResponse;
import org.codewithzea.doccasetracker.entity.ApprovalStatus;
import org.codewithzea.doccasetracker.entity.User;
import org.codewithzea.doccasetracker.exception.UserNotFoundException;
import org.codewithzea.doccasetracker.mapper.ApprovalStatusMapper;
import org.codewithzea.doccasetracker.repository.RefreshTokenRepository;
import org.codewithzea.doccasetracker.repository.UserRepository;
import org.codewithzea.doccasetracker.service.AdminService;
import org.codewithzea.doccasetracker.service.AuditLogService;
import org.codewithzea.doccasetracker.service.EmailService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.codewithzea.doccasetracker.util.AuditActions.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    private static final String USER_NOT_FOUND = "User not found with id: ";

    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final ApprovalStatusMapper approvalStatusMapper;
    private final EmailService emailService;
    private final RefreshTokenRepository refreshTokenRepository;

    // ---------------- CACHE KEYS ----------------
    private static final String CACHE_USERS = "users";
    private static final String CACHE_USER_BY_ID = "userById";
    private static final String CACHE_PENDING_USERS = "pendingUsers";
    private static final String CACHE_APPROVED_USERS = "approvedUsers";

    // ================== WRITE OPS ==================

    @Override
    @CacheEvict(value = {CACHE_USERS, CACHE_USER_BY_ID, CACHE_PENDING_USERS, CACHE_APPROVED_USERS}, allEntries = true)
    public ApprovalStatusResponse approveUser(String id) {

        User user = getUserOrThrow(id);

        user.setApprovalStatus(ApprovalStatus.APPROVED);
        user.setEnabled(true);
        user.setApprovedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        emailService.sendAccountApprovedEmail(
                user.getEmail(),
                user.getFirstName() + " " + user.getLastName()
        );

        User admin = getCurrentUser();

        auditLogService.log(
                USER_APPROVED,
                "USER",
                user.getId(),
                "Approved account for " + user.getEmail(),
                admin.getEmail(),
                admin.getId(),
                admin.getRole().getName().name()
        );

        return approvalStatusMapper.toResponse(savedUser);
    }

    @Override
    @CacheEvict(value = {CACHE_USERS, CACHE_USER_BY_ID, CACHE_PENDING_USERS, CACHE_APPROVED_USERS}, allEntries = true)
    public ApprovalStatusResponse rejectUser(String id) {

        User user = getUserOrThrow(id);

        user.setApprovalStatus(ApprovalStatus.REJECTED);
        user.setEnabled(false);

        emailService.sendAccountRejectedEmail(
                user.getEmail(),
                user.getFirstName() + " " + user.getLastName()
        );

        User savedUser = userRepository.save(user);

        User admin = getCurrentUser();

        auditLogService.log(
                USER_REJECTED,
                "USER",
                user.getId(),
                "Rejected account for " + user.getEmail(),
                admin.getEmail(),
                admin.getId(),
                admin.getRole().getName().name()
        );

        return approvalStatusMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    @CacheEvict(value = {CACHE_USERS, CACHE_USER_BY_ID, CACHE_PENDING_USERS, CACHE_APPROVED_USERS}, allEntries = true)
    public void deleteUser(String id) {

        User user = getUserOrThrow(id);

        User admin = getCurrentUser();

        auditLogService.log(
                USER_DELETED,
                "USER",
                user.getId(),
                "Deleted account for " + user.getEmail(),
                admin.getEmail(),
                admin.getId(),
                admin.getRole().getName().name()
        );

        refreshTokenRepository.deleteAllByUserId(user.getId());

        userRepository.delete(user);
    }

    // ================== READ OPS (CACHED) ==================

    @Override
    @Transactional(readOnly = true)
//    @Cacheable(value = CACHE_USERS, key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ApprovalStatusResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(approvalStatusMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
//    @Cacheable(value = CACHE_PENDING_USERS, key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ApprovalStatusResponse> getPendingUsers(Pageable pageable) {
        return userRepository.findAllByApprovalStatus(ApprovalStatus.PENDING, pageable)
                .map(approvalStatusMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
//    @Cacheable(value = CACHE_APPROVED_USERS, key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ApprovalStatusResponse> getApprovedUsers(Pageable pageable) {
        return userRepository.findAllByApprovalStatus(ApprovalStatus.APPROVED, pageable)
                .map(approvalStatusMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CACHE_USER_BY_ID, key = "#id")
    public ApprovalStatusResponse getUserById(String id) {

        User user = getUserOrThrow(id);
        return approvalStatusMapper.toResponse(user);
    }

    // ================== INTERNAL ==================

    private User getUserOrThrow(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND + id));
    }

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found in security context");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof User user)) {
            throw new IllegalStateException("Authenticated principal is not a valid User");
        }

        return user;
    }
}