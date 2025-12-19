package org.system.digitalwallet.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String actorType;     // USER, SYSTEM
    private Long actorId;         // user id (nullable)

    private String action;        // LOGIN, REGISTER, LOGOUT
    private String entityType;    // USER, AUTH
    private Long entityId;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String status;        // SUCCESS, FAILED

    private String ipAddress;

    private String userAgent;

    private LocalDateTime createdAt;
}
