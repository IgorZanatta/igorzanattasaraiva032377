package br.gov.mt.seplag.igorzannattasaraiva032377.service.genre;

import java.util.List;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.genre.request.GenreRequestDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.genre.response.GenreResponseDTO;

public interface GenreService {

    GenreResponseDTO create(GenreRequestDTO dto);

    List<GenreResponseDTO> findAll();

    List<GenreResponseDTO> findByName(String name, String sortDirection);
}