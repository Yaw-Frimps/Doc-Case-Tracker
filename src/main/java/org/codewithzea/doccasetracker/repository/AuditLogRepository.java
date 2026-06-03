package org.codewithzea.doccasetracker.repository;

import org.codewithzea.doccasetracker.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, String> {
}
