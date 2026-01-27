package br.gov.mt.seplag.igorzannattasaraiva032377.repository.auditLog;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.gov.mt.seplag.igorzannattasaraiva032377.entity.auditLog.AuditLogEntity;

public interface AuditLogRepository extends JpaRepository<AuditLogEntity, UUID> {
}
