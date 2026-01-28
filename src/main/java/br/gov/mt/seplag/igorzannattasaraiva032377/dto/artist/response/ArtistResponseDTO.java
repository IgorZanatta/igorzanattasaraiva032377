package br.gov.mt.seplag.igorzannattasaraiva032377.dto.artist.response;

import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistType;

import java.time.LocalDateTime;
import java.util.UUID;

public record ArtistResponseDTO(
        UUID id,
        String name,
        ArtistType type,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}