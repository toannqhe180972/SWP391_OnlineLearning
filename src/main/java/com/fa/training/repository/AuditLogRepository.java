package com.fa.training.repository;

import com.fa.training.entities.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

        @Query("SELECT a FROM AuditLog a WHERE " +
                        "(:performedBy IS NULL OR :performedBy = '' OR a.performedBy LIKE %:performedBy%) AND " +
                        "(:action IS NULL OR :action = '' OR a.action = :action)")
        Page<AuditLog> findWithFilters(@Param("performedBy") String performedBy,
                        @Param("action") String action,
                        Pageable pageable);
}
