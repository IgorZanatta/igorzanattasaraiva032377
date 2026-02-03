package br.gov.mt.seplag.igorzannattasaraiva032377.service.artist;

import java.util.List;
import java.util.UUID;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.artist.request.ArtistRequestDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.artist.response.ArtistResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistType;

public interface ArtistService {

    ArtistResponseDTO create(ArtistRequestDTO dto);

    /**
     * Lista artistas aplicando opcionalmente filtro por nome (parcial), tipo e ordenação por nome.
     * Sempre ordena pelo campo name, ascendente por padrão e descendente quando sort = "desc".
     */
    List<ArtistResponseDTO> findAll(String name, ArtistType type, String sort);

    ArtistResponseDTO update(UUID id, ArtistRequestDTO dto);
}
