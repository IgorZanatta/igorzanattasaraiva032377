-- V5__create_regional.sql

CREATE TABLE regional (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- interno
    external_id INTEGER NOT NULL,                            -- vem da API externa
    nome        VARCHAR(200) NOT NULL,
    ativo       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT ck_regional_external_id_positive CHECK (external_id > 0)
);

CREATE INDEX idx_regional_external_id ON regional(external_id);

CREATE UNIQUE INDEX uk_regional_one_active_per_external
    ON regional(external_id)
    WHERE ativo = TRUE;
