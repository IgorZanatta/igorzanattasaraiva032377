package br.gov.mt.seplag.igorzannattasaraiva032377.dto.artist.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistType;

public record ArtistResponseDTO(
                UUID id,
                String name,
                ArtistType type,
                List<String> genres,
                LocalDateTime createdAt,
                LocalDateTime updatedAt
) {}