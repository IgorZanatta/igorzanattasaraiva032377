package br.gov.mt.seplag.igorzannattasaraiva032377.mapper.genre;


import br.gov.mt.seplag.igorzannattasaraiva032377.dto.genre.request.GenreRequestDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.genre.response.GenreResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.genre.GenreEntity;

public class GenreMapper {

    private GenreMapper() {}

    public static GenreEntity toEntity(GenreRequestDTO dto) {
        return GenreEntity.builder()
                .name(dto.name())
                .active(dto.active() != null ? dto.active() : true)
                .build();
    }

    public static GenreResponseDTO toResponse(GenreEntity entity) {
        return new GenreResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.isActive()
        );
    }
}