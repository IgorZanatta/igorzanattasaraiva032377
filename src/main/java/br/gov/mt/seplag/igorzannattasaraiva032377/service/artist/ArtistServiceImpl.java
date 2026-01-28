package br.gov.mt.seplag.igorzannattasaraiva032377.service.artist;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.artist.request.ArtistRequestDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.artist.response.ArtistResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistType;
import br.gov.mt.seplag.igorzannattasaraiva032377.exception.ResourceNotFoundException;
import br.gov.mt.seplag.igorzannattasaraiva032377.mapper.artist.ArtistMapper;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.artist.ArtistRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {

    private final ArtistRepository repository;

    @Override
    public ArtistResponseDTO create(ArtistRequestDTO dto) {
        ArtistEntity entity = ArtistMapper.toEntity(dto);
        return ArtistMapper.toResponse(repository.save(entity));
    }

    @Override
    public ArtistResponseDTO findById(UUID id) {
        return repository.findById(id)
                .map(ArtistMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found"));
    }

    @Override
    public List<ArtistResponseDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(ArtistMapper::toResponse)
                .toList();
    }

    @Override
    public List<ArtistResponseDTO> findByName(String name, String sortDirection) {
        var entities = repository.findByNameContainingIgnoreCase(name);

        Comparator<ArtistEntity> comparator = Comparator.comparing(
                ArtistEntity::getName,
                String.CASE_INSENSITIVE_ORDER
        );

        if (sortDirection != null && sortDirection.equalsIgnoreCase("desc")) {
            comparator = comparator.reversed();
        }

        return entities.stream()
                .sorted(comparator)
                .map(ArtistMapper::toResponse)
                .toList();
    }

    @Override
    public List<ArtistResponseDTO> findByType(ArtistType type) {
        return repository.findByType(type)
                .stream()
                .map(ArtistMapper::toResponse)
                .toList();
    }

    @Override
    public ArtistResponseDTO update(UUID id, ArtistRequestDTO dto) {
        ArtistEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found"));

        entity.setName(dto.name());
        entity.setType(dto.type());

        return ArtistMapper.toResponse(repository.save(entity));
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Artist not found");
        }
        repository.deleteById(id);
    }
}
