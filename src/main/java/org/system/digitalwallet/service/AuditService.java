package org.system.digitalwallet.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.system.digitalwallet.model.AuditLog;
import org.system.digitalwallet.model.User;
import org.system.digitalwallet.repository.AuditLogRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void log(
            User user,
            String action,
            String entityType,
            Long entityId,
            String status,
            String description,
            HttpServletRequest request
    ) {
        AuditLog log = AuditLog.builder()
                .actorType("USER")
                .actorId(user != null ? user.getId() : null)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .status(status)
                .description(description)
                .ipAddress(request.getRemoteAddr())
                .userAgent(request.getHeader("User-Agent"))
                .createdAt(LocalDateTime.now())
                .build();

        auditLogRepository.save(log);
    }

    public void logAnonymous(
            String action,
            String status,
            String description,
            HttpServletRequest request
    ) {
        AuditLog log = AuditLog.builder()
                .actorType("ANONYMOUS")
                .action(action)
                .entityType("AUTH")
                .status(status)
                .description(description)
                .ipAddress(request.getRemoteAddr())
                .userAgent(request.getHeader("User-Agent"))
                .createdAt(LocalDateTime.now())
                .build();

        auditLogRepository.save(log);
    }
}
