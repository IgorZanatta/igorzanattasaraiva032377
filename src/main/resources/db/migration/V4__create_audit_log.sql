-- V4__create_audit_log.sql

CREATE TABLE audit_log (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entity_name  VARCHAR(100) NOT NULL,
    entity_id    VARCHAR(64)  NOT NULL,
    action       VARCHAR(30)  NOT NULL,
    old_data     JSONB NULL,
    new_data     JSONB NULL,
    performed_by UUID NULL,
    performed_at TIMESTAMP NOT NULL DEFAULT now(),
    ip_address   VARCHAR(45) NULL,
    user_agent   VARCHAR(400) NULL,
    CONSTRAINT fk_audit_log_user FOREIGN KEY (performed_by) REFERENCES app_user(id) ON DELETE SET NULL
);

CREATE INDEX idx_audit_entity ON audit_log(entity_name, entity_id);
CREATE INDEX idx_audit_performed_by ON audit_log(performed_by);
CREATE INDEX idx_audit_performed_at ON audit_log(performed_at);
