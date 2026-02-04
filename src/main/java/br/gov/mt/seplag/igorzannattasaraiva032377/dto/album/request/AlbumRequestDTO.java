package br.gov.mt.seplag.igorzannattasaraiva032377.dto.album.request;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para criação ou atualização de álbum")
public record AlbumRequestDTO(

        @Schema(
            description = "Título do álbum",
            example = "Abbey Road",
            maxLength = 255,
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank
        @Size(max = 255)
        String title,

        @Schema(
            description = "Ano de lançamento do álbum (opcional)",
            example = "1969",
            minimum = "1900",
            maximum = "2100"
        )
        Integer releaseYear,

        @Schema(
            description = "Lista de IDs de artistas a serem vinculados ao álbum (opcional). Se não fornecida, o álbum é criado sem artistas associados.",
            example = "[\"a1f3c1c4-2c5d-4a7b-9e8c-123456789abc\", \"b2e4d5f6-3a2b-4c1d-9e8f-abcdef123456\"]"
        )
        List<UUID> artistIds
) {
}