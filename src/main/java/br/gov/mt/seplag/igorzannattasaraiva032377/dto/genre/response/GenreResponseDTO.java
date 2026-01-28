package br.gov.mt.seplag.igorzannattasaraiva032377.dto.genre.response;

import java.util.UUID;

public record GenreResponseDTO(
        UUID id,
        String name,
        boolean active
) {}