package br.gov.mt.seplag.igorzannattasaraiva032377.dto.album.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlbumRequestDTO(

        @NotBlank
        @Size(max = 255)
        String title,

        Integer releaseYear
) {
}