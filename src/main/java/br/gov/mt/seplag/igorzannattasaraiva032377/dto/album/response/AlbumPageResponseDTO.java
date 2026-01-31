package br.gov.mt.seplag.igorzannattasaraiva032377.dto.album.response;

import java.util.List;

public record AlbumPageResponseDTO(
        List<AlbumResponseDTO> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
