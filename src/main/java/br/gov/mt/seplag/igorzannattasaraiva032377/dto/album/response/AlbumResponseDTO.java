package br.gov.mt.seplag.igorzannattasaraiva032377.dto.album.response;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados completos de um álbum musical")
public record AlbumResponseDTO(

        @Schema(
            description = "Identificador único do álbum",
            example = "123e4567-e89b-12d3-a456-426614174000"
        )
        UUID id,
        
        @Schema(
            description = "Título do álbum",
            example = "Abbey Road"
        )
        String title,
        
        @Schema(
            description = "Ano de lançamento",
            example = "1969"
        )
        Integer releaseYear,
        
        @Schema(
            description = "Data e hora de criação do registro",
            example = "2024-02-04T10:30:00"
        )
        LocalDateTime createdAt,
        
        @Schema(
            description = "Data e hora da última atualização",
            example = "2024-02-04T15:45:30"
        )
        LocalDateTime updatedAt
) {
}