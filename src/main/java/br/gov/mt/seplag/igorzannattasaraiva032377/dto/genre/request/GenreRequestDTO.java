package br.gov.mt.seplag.igorzannattasaraiva032377.dto.genre.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GenreRequestDTO(

        @NotBlank
        @Size(max = 120)
        String name,

        Boolean active
) {}