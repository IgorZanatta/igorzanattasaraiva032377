package br.gov.mt.seplag.igorzannattasaraiva032377.dto.artist.request;


import java.util.List;

import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para criação ou atualização de artista")
public record ArtistRequestDTO(

                @Schema(
                    description = "Nome do artista ou banda",
                    example = "The Beatles",
                    maxLength = 255,
                    requiredMode = Schema.RequiredMode.REQUIRED
                )
                @NotBlank
                @Size(max = 255)
                String name,

                @Schema(
                    description = "Tipo do artista: SOLO (individual) ou BAND (banda/grupo)",
                    allowableValues = {"SOLO", "BAND"},
                    example = "BAND",
                    requiredMode = Schema.RequiredMode.REQUIRED
                )
                @NotNull
                ArtistType type,

                @Schema(
                    description = "Lista de nomes de gêneros musicais associados ao artista. Gêneros inexistentes serão criados automaticamente.",
                    example = "[\"Rock\", \"Pop\", \"Blues\"]"
                )
                List<String> genres
) {}