package br.gov.mt.seplag.igorzannattasaraiva032377.service.album;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.album.request.AlbumRequestDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.album.response.AlbumResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AlbumService {

    AlbumResponseDTO create(AlbumRequestDTO dto);

    AlbumResponseDTO findById(UUID id);

    Page<AlbumResponseDTO> findAll(Pageable pageable);

    Page<AlbumResponseDTO> findByTitle(String title, Pageable pageable);

    Page<AlbumResponseDTO> findByReleaseYear(Integer year, Pageable pageable);

    AlbumResponseDTO update(UUID id, AlbumRequestDTO dto);

    void delete(UUID id);
}