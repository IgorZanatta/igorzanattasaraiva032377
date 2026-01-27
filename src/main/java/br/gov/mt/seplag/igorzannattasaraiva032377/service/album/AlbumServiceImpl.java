package br.gov.mt.seplag.igorzannattasaraiva032377.service.album;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.album.request.AlbumRequestDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.album.response.AlbumResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.album.AlbumEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.mapper.album.AlbumMapper;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.album.AlbumRepository;
import br.gov.mt.seplag.igorzannattasaraiva032377.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;

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
    public List<AlbumResponseDTO> findAll() {
        return albumRepository.findAll()
                .stream()
                .map(AlbumMapper::toResponse)
                .toList();
    }

    @Override
    public List<AlbumResponseDTO> findByTitle(String title) {
        return albumRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(AlbumMapper::toResponse)
                .toList();
    }

    @Override
    public List<AlbumResponseDTO> findByReleaseYear(Integer year) {
        return albumRepository.findByReleaseYear(year)
                .stream()
                .map(AlbumMapper::toResponse)
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
