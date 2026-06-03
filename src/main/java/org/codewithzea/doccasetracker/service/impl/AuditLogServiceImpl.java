package org.codewithzea.doccasetracker.service.impl;

import lombok.RequiredArgsConstructor;
import org.codewithzea.doccasetracker.entity.AuditLog;
import org.codewithzea.doccasetracker.repository.AuditLogRepository;
import org.codewithzea.doccasetracker.service.AuditLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Async
    @Transactional
    public void log(String action, String performedBy) {
        AuditLog auditLog = AuditLog.builder()
                .action(action)
                .performedBy(performedBy)
                .build();
        auditLogRepository.save(auditLog);
    }
}
