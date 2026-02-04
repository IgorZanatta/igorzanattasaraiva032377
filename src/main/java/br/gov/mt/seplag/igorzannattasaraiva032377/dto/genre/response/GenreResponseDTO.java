package br.gov.mt.seplag.igorzannattasaraiva032377.dto.genre.response;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de um gênero musical")
public record GenreResponseDTO(
        @Schema(
            description = "Identificador único do gênero",
            example = "123e4567-e89b-12d3-a456-426614174000"
        )
        UUID id,
        
        @Schema(
            description = "Nome do gênero musical",
            example = "Rock"
        )
        String name,
        
        @Schema(
            description = "Status do gênero (ativo/inativo)",
            example = "true"
        )
        boolean active
) {}