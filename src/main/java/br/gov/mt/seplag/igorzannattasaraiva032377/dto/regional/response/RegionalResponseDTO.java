package br.gov.mt.seplag.igorzannattasaraiva032377.dto.regional.response;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "Dados de uma regional administrativa obtida via integração externa")
@Getter
@AllArgsConstructor
public class RegionalResponseDTO {

    @Schema(
        description = "Identificador único interno da regional",
        example = "123e4567-e89b-12d3-a456-426614174000"
    )
    private UUID id;
    
    @Schema(
        description = "ID externo da regional no sistema de origem (API Argus)",
        example = "42"
    )
    private Integer externalId;
    
    @Schema(
        description = "Nome da regional administrativa",
        example = "Regional de Cuiabá"
    )
    private String nome;
    
    @Schema(
        description = "Status de ativação da regional",
        example = "true"
    )
    private Boolean ativo;
}