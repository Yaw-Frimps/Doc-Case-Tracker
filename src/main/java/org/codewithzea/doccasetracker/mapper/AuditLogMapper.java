package org.codewithzea.doccasetracker.mapper;


import org.codewithzea.doccasetracker.dto.response.AuditLogResponse;
import org.codewithzea.doccasetracker.entity.AuditLog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {

    AuditLogResponse toResponse(AuditLog auditLog);
}
