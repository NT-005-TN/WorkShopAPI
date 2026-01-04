package com.jewelry.workshop.domain.repository;

import com.jewelry.workshop.domain.model.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public class AuditLogRepository extends JpaRepository<AuditLog, Long> {

}
