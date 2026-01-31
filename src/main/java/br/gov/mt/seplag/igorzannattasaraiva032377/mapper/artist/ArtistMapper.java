package br.gov.mt.seplag.igorzannattasaraiva032377.mapper.artist;

import java.util.List;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.artist.request.ArtistRequestDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.artist.response.ArtistResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistEntity;

public class ArtistMapper {

    private ArtistMapper() {}

    public static ArtistEntity toEntity(ArtistRequestDTO dto) {
        return ArtistEntity.builder()
                .name(dto.name())
                .type(dto.type())
                .build();
    }

    public static ArtistResponseDTO toResponse(ArtistEntity entity, List<String> genres) {
        return new ArtistResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getType(),
                genres,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}