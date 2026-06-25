package org.codewithzea.doccasetracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 255)
    private String action;

    @Column(name = "performed_by_email", nullable = false, length = 150)
    private String performedBy;

    @Column(name = "performed_by_userId", nullable = false)
    private String performedByUserId;

    private String entityType;

    private String entityId;

    private String role;

    private String details;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
}
