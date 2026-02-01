package br.gov.mt.seplag.igorzannattasaraiva032377.dto.album.response;

import java.util.List;
import java.util.UUID;

public record AlbumWithArtistsResponseDTO(

        UUID id,
        String title,
        Integer releaseYear,
        List<String> artists
) {
}
