package br.gov.mt.seplag.igorzannattasaraiva032377.dto.artist.request;


import java.util.List;

import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ArtistRequestDTO(

                @NotBlank
                @Size(max = 255)
                String name,

                @NotNull
                ArtistType type,

                List<String> genres
) {}