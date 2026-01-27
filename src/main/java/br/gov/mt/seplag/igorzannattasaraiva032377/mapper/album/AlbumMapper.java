package br.gov.mt.seplag.igorzannattasaraiva032377.mapper.album;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.album.request.AlbumRequestDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.album.response.AlbumResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.album.AlbumEntity;

public class AlbumMapper {

    private AlbumMapper() {}

    public static AlbumEntity toEntity(AlbumRequestDTO dto) {
        return AlbumEntity.builder()
                .title(dto.title())
                .releaseYear(dto.releaseYear())
                .build();
    }

    public static AlbumResponseDTO toResponse(AlbumEntity entity) {
        return new AlbumResponseDTO(
                entity.getId(),
                entity.getTitle(),
                entity.getReleaseYear(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}