package org.codewithzea.doccasetracker.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AuditLogResponse {

    private String id;

    private String action;

    private String entityType;

    private String entityId;

    private String performedBy;

    private String performedByUserId;

    private String role;

    private String details;

    private LocalDateTime timestamp;
}
