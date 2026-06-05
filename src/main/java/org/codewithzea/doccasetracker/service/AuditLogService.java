package org.codewithzea.doccasetracker.service;

import org.codewithzea.doccasetracker.dto.response.AuditLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {
    void log(
            String action,
            String entityType,
            String entityId,
            String details,
            String performedBy,
            String performedByUserId,
            String role
    );

    Page<AuditLogResponse> getLogs(Pageable pageable);

    Page<AuditLogResponse> getRecentLogs(int limit);

    Page<AuditLogResponse> getLogsByUser(String email, Pageable pageable);
    Page<AuditLogResponse> getLogsByUserId(String userId, Pageable pageable);
}
