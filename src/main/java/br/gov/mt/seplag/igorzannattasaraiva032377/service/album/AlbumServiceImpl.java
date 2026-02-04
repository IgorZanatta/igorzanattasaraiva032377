package br.gov.mt.seplag.igorzannattasaraiva032377.service.album;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.album.request.AlbumRequestDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.album.response.AlbumResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.album.AlbumEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.exception.ResourceNotFoundException;
import br.gov.mt.seplag.igorzannattasaraiva032377.mapper.album.AlbumMapper;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.album.AlbumRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final br.gov.mt.seplag.igorzannattasaraiva032377.service.artistAlbum.ArtistAlbumService artistAlbumService;

    @Override
    public AlbumResponseDTO create(AlbumRequestDTO dto) {
        log.info("Criando novo álbum: {}", dto.title());
        AlbumEntity entity = AlbumMapper.toEntity(dto);
        AlbumEntity saved = albumRepository.save(entity);
        AlbumResponseDTO response = AlbumMapper.toResponse(saved);

        // Vincula artistas ao álbum se fornecidos (opcional)
        if (dto.artistIds() != null && !dto.artistIds().isEmpty()) {
            log.info("Vinculando {} artista(s) ao álbum {}", dto.artistIds().size(), saved.getId());
            for (UUID artistId : dto.artistIds()) {
                try {
                    artistAlbumService.linkArtistToAlbum(artistId, saved.getId());
                    log.debug("Artista {} vinculado ao álbum {}", artistId, saved.getId());
                } catch (Exception e) {
                    log.warn("Erro ao vincular artista {} ao álbum {}: {}", artistId, saved.getId(), e.getMessage());
                    // Continua vinculando os demais artistas mesmo se um falhar
                }
            }
        } else {
            log.debug("Álbum criado sem artistas vinculados");
        }

        log.info("Enviando notificação WebSocket para /topic/albums/new, id={}", response.id());
        messagingTemplate.convertAndSend("/topic/albums/new", response);

        return response;
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
    public Page<AlbumResponseDTO> findByArtistType(br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistType type, Pageable pageable) {
        return albumRepository.findByArtistType(type, pageable)
                .map(AlbumMapper::toResponse);
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
