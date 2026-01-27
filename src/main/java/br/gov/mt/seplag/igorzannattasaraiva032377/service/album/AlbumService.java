package br.gov.mt.seplag.igorzannattasaraiva032377.service.album;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.album.request.AlbumRequestDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.album.response.AlbumResponseDTO;

import java.util.List;
import java.util.UUID;

public interface AlbumService {

    AlbumResponseDTO create(AlbumRequestDTO dto);

    AlbumResponseDTO findById(UUID id);

    List<AlbumResponseDTO> findAll();

    List<AlbumResponseDTO> findByTitle(String title);

    List<AlbumResponseDTO> findByReleaseYear(Integer year);

    AlbumResponseDTO update(UUID id, AlbumRequestDTO dto);

    void delete(UUID id);
}