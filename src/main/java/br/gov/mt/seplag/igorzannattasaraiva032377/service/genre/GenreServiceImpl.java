package br.gov.mt.seplag.igorzannattasaraiva032377.service.genre;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.genre.request.GenreRequestDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.genre.response.GenreResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.genre.GenreEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.mapper.genre.GenreMapper;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.genre.GenreRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository repository;

    @Override
    public GenreResponseDTO create(GenreRequestDTO dto) {
        GenreEntity entity = GenreMapper.toEntity(dto);
        return GenreMapper.toResponse(repository.save(entity));
    }

    @Override
    public List<GenreResponseDTO> findAll() {
        return repository.findAll()
            .stream()
            .sorted(Comparator.comparing(GenreEntity::getName, String.CASE_INSENSITIVE_ORDER))
            .map(GenreMapper::toResponse)
            .toList();
        }

        @Override
        public List<GenreResponseDTO> findByName(String name, String sortDirection) {
        var entities = repository.findByNameContainingIgnoreCase(name);

        Comparator<GenreEntity> comparator = Comparator.comparing(
            GenreEntity::getName,
            String.CASE_INSENSITIVE_ORDER
        );

        if (sortDirection != null && sortDirection.equalsIgnoreCase("desc")) {
            comparator = comparator.reversed();
        }

        return entities.stream()
            .sorted(comparator)
            .map(GenreMapper::toResponse)
            .toList();
    }
}
