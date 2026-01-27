package br.gov.mt.seplag.igorzannattasaraiva032377.dto.album.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record AlbumResponseDTO(

        UUID id,
        String title,
        Integer releaseYear,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}