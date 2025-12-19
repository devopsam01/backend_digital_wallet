package org.system.digitalwallet.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.system.digitalwallet.model.AuditLog;

import java.time.LocalDateTime;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findByActorIdOrderByCreatedAtDesc(Long actorId, Pageable pageable);

    Page<AuditLog> findByEntityTypeOrderByCreatedAtDesc(String entityType, Pageable pageable);

    // 📊 Count recent audit logs
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.createdAt >= :since")
    long countRecentAudits(LocalDateTime since);
}
