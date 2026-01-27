package br.gov.mt.seplag.igorzannattasaraiva032377.service.auditLog;

public interface AuditLogService {

    void log(String entityName, String entityId, String action, Object oldData, Object newData);
}
