-- V3__create_album_cover.sql

CREATE TABLE album_cover (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    album_id     UUID NOT NULL,
    object_key   VARCHAR(500) NOT NULL,
    content_type VARCHAR(100) NULL,
    file_size    BIGINT NULL,
    is_primary   BOOLEAN NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMP NOT NULL DEFAULT now(),
    updated_at   TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_album_cover_album
        FOREIGN KEY (album_id) REFERENCES album(id) ON DELETE CASCADE,
    CONSTRAINT uk_album_cover_album_object
        UNIQUE (album_id, object_key)
);

CREATE INDEX idx_album_cover_album_id ON album_cover(album_id);

CREATE UNIQUE INDEX uk_album_cover_primary_per_album
    ON album_cover(album_id)
    WHERE is_primary = TRUE;
