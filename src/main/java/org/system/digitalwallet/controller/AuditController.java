package org.system.digitalwallet.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.system.digitalwallet.model.AuditLog;
import org.system.digitalwallet.model.User;
import org.system.digitalwallet.repository.AuditLogRepository;

@Slf4j
@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditLogRepository auditLogRepository;

    // 🔍 Get audit logs for current user (PAGINATED)
    @GetMapping("/me")
    public ResponseEntity<Page<AuditLog>> getMyAuditLogs(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("➡️ Fetching audit logs for user={}, page={}, size={}",
                user.getUsername(), page, size);

        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<AuditLog> logs =
                auditLogRepository.findByActorIdOrderByCreatedAtDesc(
                        user.getId(),
                        pageable
                );

        return ResponseEntity.ok(logs);
    }

    // 🔍 Admin-style endpoint by entity type (PAGINATED)
    @GetMapping("/entity/{entityType}")
    public ResponseEntity<Page<AuditLog>> getAuditByEntity(
            @PathVariable String entityType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<AuditLog> logs =
                auditLogRepository.findByEntityTypeOrderByCreatedAtDesc(
                        entityType.toUpperCase(),
                        pageable
                );

        return ResponseEntity.ok(logs);
    }
}
