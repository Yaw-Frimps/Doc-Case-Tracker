package org.codewithzea.doccasetracker.service;

public interface AuditLogService {
    void log(String action, String performedBy);
}
