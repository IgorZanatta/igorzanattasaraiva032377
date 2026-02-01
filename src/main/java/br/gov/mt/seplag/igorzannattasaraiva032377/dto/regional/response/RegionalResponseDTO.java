package br.gov.mt.seplag.igorzannattasaraiva032377.dto.regional.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class RegionalResponseDTO {

    private UUID id;
    private Integer externalId;
    private String nome;
    private Boolean ativo;
}