package org.codewithzea.doccasetracker.service.impl;

import lombok.RequiredArgsConstructor;
import org.codewithzea.doccasetracker.dto.response.AuditLogResponse;
import org.codewithzea.doccasetracker.entity.AuditLog;
import org.codewithzea.doccasetracker.mapper.AuditLogMapper;
import org.codewithzea.doccasetracker.repository.AuditLogRepository;
import org.codewithzea.doccasetracker.service.AuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    @Override
    @Async
    @Transactional
    public void log(
            String action,
            String entityType,
            String entityId,
            String details,
            String performedBy,
            String performedByUserId,
            String role
    ) {

        AuditLog auditLog = AuditLog.builder()
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .performedBy(performedBy)
                .performedByUserId(performedByUserId)
                .role(role)
                .details(details)
                .timestamp(LocalDateTime.now())
                .build();

        auditLogRepository.save(auditLog);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getLogs(Pageable pageable) {

        return auditLogRepository.findAllByOrderByTimestampDesc(pageable)
                .map(auditLogMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getRecentLogs(int limit) {

        Pageable pageable = Pageable.ofSize(limit);

        return auditLogRepository
                .findAllByOrderByTimestampDesc(pageable)
                .map(auditLogMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getLogsByUser(String email, Pageable pageable) {

        return auditLogRepository
                .findByPerformedByOrderByTimestampDesc(email, pageable)
                .map(auditLogMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getLogsByUserId(String userId, Pageable pageable) {

        return auditLogRepository
                .findByPerformedByUserIdOrderByTimestampDesc(userId, pageable)
                .map(auditLogMapper::toResponse);
    }
}
