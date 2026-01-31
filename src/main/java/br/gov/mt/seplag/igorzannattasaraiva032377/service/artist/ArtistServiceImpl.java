package br.gov.mt.seplag.igorzannattasaraiva032377.service.artist;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.artist.request.ArtistRequestDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.artist.response.ArtistResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistType;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.genre.GenreEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.exception.ResourceNotFoundException;
import br.gov.mt.seplag.igorzannattasaraiva032377.mapper.artist.ArtistMapper;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.artist.ArtistRepository;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.genre.GenreRepository;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.artistGenre.ArtistGenreService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {

    private final ArtistRepository repository;
    private final ArtistGenreService artistGenreService;
    private final GenreRepository genreRepository;

    @Override
    public ArtistResponseDTO create(ArtistRequestDTO dto) {
        ArtistEntity entity = ArtistMapper.toEntity(dto);
        entity = repository.save(entity);

        linkGenresFromNames(entity.getId(), dto.genres());

        return buildResponseWithGenres(entity);
    }

    @Override
    public ArtistResponseDTO findById(UUID id) {
        ArtistEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found"));
        return buildResponseWithGenres(entity);
    }

    @Override
    public List<ArtistResponseDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(this::buildResponseWithGenres)
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
                .map(this::buildResponseWithGenres)
                .toList();
    }

    @Override
    public List<ArtistResponseDTO> findByType(ArtistType type) {
        return repository.findByType(type)
                .stream()
                .map(this::buildResponseWithGenres)
                .toList();
    }

    @Override
    public ArtistResponseDTO update(UUID id, ArtistRequestDTO dto) {
        ArtistEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found"));

        entity.setName(dto.name());
        entity.setType(dto.type());
        entity = repository.save(entity);

        if (dto.genres() != null) {
            // Atualiza completamente o conjunto de gêneros com base nos nomes enviados
            updateGenresFromNames(entity.getId(), dto.genres());
        }

        return buildResponseWithGenres(entity);
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Artist not found");
        }
        repository.deleteById(id);
    }

    private ArtistResponseDTO buildResponseWithGenres(ArtistEntity entity) {
        var genreIds = artistGenreService.getGenreIdsByArtist(entity.getId());
        var genreNames = genreIds.stream()
                .map(genreRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(GenreEntity::getName)
                .toList();
        return ArtistMapper.toResponse(entity, genreNames);
    }

    private void linkGenresFromNames(UUID artistId, List<String> genreNames) {
        if (genreNames == null || genreNames.isEmpty()) {
            return;
        }
        genreNames.stream()
                .filter(name -> name != null && !name.isBlank())
                .map(String::trim)
                .distinct()
                .forEach(name -> {
                    GenreEntity genre = genreRepository.findByName(name)
                            .orElseThrow(() -> new ResourceNotFoundException("Genre not found with name: " + name));
                    artistGenreService.linkArtistToGenre(artistId, genre.getId());
                });
    }

    private void updateGenresFromNames(UUID artistId, List<String> genreNames) {
        // Conjunto desejado a partir dos nomes enviados
        Set<UUID> desired = new HashSet<>();
        if (genreNames != null) {
            genreNames.stream()
                    .filter(name -> name != null && !name.isBlank())
                    .map(String::trim)
                    .distinct()
                    .forEach(name -> {
                        GenreEntity genre = genreRepository.findByName(name)
                                .orElseThrow(() -> new ResourceNotFoundException("Genre not found with name: " + name));
                        desired.add(genre.getId());
                    });
        }

        // Conjunto atual de gêneros do artista
        Set<UUID> current = new HashSet<>(artistGenreService.getGenreIdsByArtist(artistId));

        // Adiciona vínculos faltantes
        desired.stream()
                .filter(id -> !current.contains(id))
                .forEach(id -> artistGenreService.linkArtistToGenre(artistId, id));

        // Remove vínculos que não estão mais desejados
        current.stream()
                .filter(id -> !desired.contains(id))
                .forEach(id -> artistGenreService.unlinkArtistFromGenre(artistId, id));
    }
}
