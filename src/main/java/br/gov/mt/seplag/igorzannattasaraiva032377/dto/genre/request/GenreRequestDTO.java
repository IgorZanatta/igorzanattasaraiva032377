package br.gov.mt.seplag.igorzannattasaraiva032377.dto.genre.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para criação de gênero musical")
public record GenreRequestDTO(

        @Schema(
            description = "Nome do gênero musical",
            example = "Rock",
            maxLength = 120,
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank
        @Size(max = 120)
        String name,

        @Schema(
            description = "Indica se o gênero está ativo (true por padrão)",
            example = "true",
            defaultValue = "true"
        )
        Boolean active
) {}