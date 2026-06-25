package org.codewithzea.doccasetracker.repository;

import org.codewithzea.doccasetracker.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, String> {

    Page<AuditLog> findAllByOrderByTimestampDesc(Pageable pageable);

    Page<AuditLog> findByPerformedByOrderByTimestampDesc(String performedBy, Pageable pageable);
    Page<AuditLog> findByPerformedByUserIdOrderByTimestampDesc(String performedByUserId, Pageable pageable);
}
