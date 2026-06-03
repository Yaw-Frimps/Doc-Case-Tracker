package org.codewithzea.doccasetracker.service.impl;

import lombok.RequiredArgsConstructor;
import org.codewithzea.doccasetracker.dto.response.ApprovalStatusResponse;
import org.codewithzea.doccasetracker.entity.ApprovalStatus;
import org.codewithzea.doccasetracker.entity.User;
import org.codewithzea.doccasetracker.exception.UserNotFoundException;
import org.codewithzea.doccasetracker.mapper.ApprovalStatusMapper;
import org.codewithzea.doccasetracker.repository.UserRepository;
import org.codewithzea.doccasetracker.service.AdminService;
import org.codewithzea.doccasetracker.service.AuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    private static final String USER_NOT_FOUND = "User not found with id: ";

    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final ApprovalStatusMapper approvalStatusMapper;


    @Override
    public ApprovalStatusResponse approveUser(String id) {

        User user = getUserOrThrow(id);

        user.setApprovalStatus(ApprovalStatus.APPROVED);
        user.setEnabled(true);
        user.setApprovedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        auditLogService.log(
                "User Approved",
                savedUser.getEmail()
        );

        return approvalStatusMapper.toResponse(savedUser);
    }

    @Override
    public ApprovalStatusResponse rejectUser(String id) {

        User user = getUserOrThrow(id);

        user.setApprovalStatus(ApprovalStatus.REJECTED);
        user.setEnabled(false);

        User savedUser = userRepository.save(user);

        auditLogService.log(
                "User Rejected",
                savedUser.getEmail()
        );

        return approvalStatusMapper.toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApprovalStatusResponse> getAllUsers(Pageable pageable) {

        return userRepository.findAll(pageable)
                .map(approvalStatusMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApprovalStatusResponse> getPendingUsers(Pageable pageable) {

        return userRepository
                .findAllByApprovalStatus(
                        ApprovalStatus.PENDING,
                        pageable
                )
                .map(approvalStatusMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApprovalStatusResponse> getApprovedUsers(Pageable pageable) {

        return userRepository
                .findAllByApprovalStatus(
                        ApprovalStatus.APPROVED,
                        pageable
                )
                .map(approvalStatusMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ApprovalStatusResponse getUserById(String id) {

        User user = getUserOrThrow(id);

        return approvalStatusMapper.toResponse(user);
    }

    @Override
    public void deleteUser(String id) {

        User user = getUserOrThrow(id);

        auditLogService.log(
                "User Deleted",
                user.getEmail()
        );

        userRepository.delete(user);
    }


    private User getUserOrThrow(String id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(USER_NOT_FOUND + id));
    }
}