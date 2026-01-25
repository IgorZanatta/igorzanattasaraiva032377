-- V2__create_relationship_tables.sql

-- artist_album (N:N)
CREATE TABLE artist_album (
    artist_id  UUID NOT NULL,
    album_id   UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT pk_artist_album PRIMARY KEY (artist_id, album_id),
    CONSTRAINT fk_artist_album_artist FOREIGN KEY (artist_id) REFERENCES artist(id) ON DELETE CASCADE,
    CONSTRAINT fk_artist_album_album  FOREIGN KEY (album_id)  REFERENCES album(id)  ON DELETE CASCADE
);

-- Índice para buscar artistas por álbum
CREATE INDEX idx_artist_album_album_id ON artist_album(album_id);

-- artist_genre (N:N)
CREATE TABLE artist_genre (
    artist_id  UUID NOT NULL,
    genre_id   UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT pk_artist_genre PRIMARY KEY (artist_id, genre_id),
    CONSTRAINT fk_artist_genre_artist FOREIGN KEY (artist_id) REFERENCES artist(id) ON DELETE CASCADE,
    CONSTRAINT fk_artist_genre_genre  FOREIGN KEY (genre_id)  REFERENCES genre(id)  ON DELETE RESTRICT
);

-- Índice para buscar artistas por gênero
CREATE INDEX idx_artist_genre_genre_id ON artist_genre(genre_id);
