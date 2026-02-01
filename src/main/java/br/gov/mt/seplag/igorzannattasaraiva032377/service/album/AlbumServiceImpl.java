package br.gov.mt.seplag.igorzannattasaraiva032377.service.album;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.album.request.AlbumRequestDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.album.response.AlbumResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.album.response.AlbumWithArtistsResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.album.AlbumEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.exception.ResourceNotFoundException;
import br.gov.mt.seplag.igorzannattasaraiva032377.mapper.album.AlbumMapper;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.album.AlbumRepository;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.artist.ArtistRepository;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.artistAlbum.ArtistAlbumService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistAlbumService artistAlbumService;
    private final ArtistRepository artistRepository;

    @Override
    public AlbumResponseDTO create(AlbumRequestDTO dto) {
        AlbumEntity entity = AlbumMapper.toEntity(dto);
        return AlbumMapper.toResponse(albumRepository.save(entity));
    }

    @Override
    public AlbumResponseDTO findById(UUID id) {
        AlbumEntity entity = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found"));
        return AlbumMapper.toResponse(entity);
    }

    @Override
    public Page<AlbumResponseDTO> findAll(Pageable pageable) {
        return albumRepository.findAll(pageable)
                .map(AlbumMapper::toResponse);
    }

    @Override
    public Page<AlbumResponseDTO> findByTitle(String title, Pageable pageable) {
        return albumRepository.findByTitleContainingIgnoreCase(title, pageable)
                .map(AlbumMapper::toResponse);
    }

    @Override
    public Page<AlbumResponseDTO> findByReleaseYear(Integer year, Pageable pageable) {
        return albumRepository.findByReleaseYear(year, pageable)
                .map(AlbumMapper::toResponse);
    }

        @Override
        public List<AlbumWithArtistsResponseDTO> findAllWithArtists() {
        return albumRepository.findAll().stream()
            .map(album -> {
                var artistIds = artistAlbumService.getArtistIdsByAlbum(album.getId());
                List<ArtistEntity> artists = artistRepository.findAllById(artistIds);
                List<String> artistNames = artists.stream()
                    .map(ArtistEntity::getName)
                    .toList();

                return new AlbumWithArtistsResponseDTO(
                    album.getId(),
                    album.getTitle(),
                    album.getReleaseYear(),
                    artistNames
                );
            })
            .toList();
        }

    @Override
    public AlbumResponseDTO update(UUID id, AlbumRequestDTO dto) {
        AlbumEntity entity = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found"));

        entity.setTitle(dto.title());
        entity.setReleaseYear(dto.releaseYear());

        return AlbumMapper.toResponse(albumRepository.save(entity));
    }

    @Override
    public void delete(UUID id) {
        AlbumEntity entity = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found"));
        albumRepository.delete(entity);
    }
}
