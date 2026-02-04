package br.gov.mt.seplag.igorzannattasaraiva032377.dto.artist.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados completos de um artista musical")
public record ArtistResponseDTO(
                @Schema(
                    description = "Identificador único do artista",
                    example = "123e4567-e89b-12d3-a456-426614174000"
                )
                UUID id,
                
                @Schema(
                    description = "Nome do artista ou banda",
                    example = "The Beatles"
                )
                String name,
                
                @Schema(
                    description = "Tipo do artista",
                    allowableValues = {"SOLO", "BAND"},
                    example = "BAND"
                )
                ArtistType type,
                
                @Schema(
                    description = "Lista de nomes de gêneros musicais associados",
                    example = "[\"Rock\", \"Pop\"]"
                )
                List<String> genres,
                
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
) {}