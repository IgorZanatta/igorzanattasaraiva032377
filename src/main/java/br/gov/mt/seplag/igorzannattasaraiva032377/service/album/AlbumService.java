package br.gov.mt.seplag.igorzannattasaraiva032377.service.album;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.album.request.AlbumRequestDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.album.response.AlbumResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.album.response.AlbumWithArtistsResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistType;

public interface AlbumService {

    AlbumResponseDTO create(AlbumRequestDTO dto);

    AlbumResponseDTO findById(UUID id);

    Page<AlbumResponseDTO> findAll(Pageable pageable);

    Page<AlbumResponseDTO> findByTitle(String title, Pageable pageable);

    Page<AlbumResponseDTO> findByReleaseYear(Integer year, Pageable pageable);

    Page<AlbumResponseDTO> findByArtistType(ArtistType type, Pageable pageable);

    List<AlbumWithArtistsResponseDTO> findAllWithArtists();

    AlbumResponseDTO update(UUID id, AlbumRequestDTO dto);

    void delete(UUID id);
}