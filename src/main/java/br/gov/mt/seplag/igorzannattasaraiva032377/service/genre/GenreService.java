package br.gov.mt.seplag.igorzannattasaraiva032377.service.genre;

import java.util.List;
import java.util.UUID;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.genre.request.GenreRequestDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.genre.response.GenreResponseDTO;

public interface GenreService {

    GenreResponseDTO create(GenreRequestDTO dto);

    GenreResponseDTO findById(UUID id);

    List<GenreResponseDTO> findAll();

    List<GenreResponseDTO> findByName(String name, String sortDirection);

    GenreResponseDTO update(UUID id, GenreRequestDTO dto);

    void deactivate(UUID id);
}