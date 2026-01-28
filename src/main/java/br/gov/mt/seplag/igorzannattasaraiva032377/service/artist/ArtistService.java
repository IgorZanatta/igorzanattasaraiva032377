package br.gov.mt.seplag.igorzannattasaraiva032377.service.artist;

import java.util.List;
import java.util.UUID;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.artist.request.ArtistRequestDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.artist.response.ArtistResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistType;

public interface ArtistService {

    ArtistResponseDTO create(ArtistRequestDTO dto);

    ArtistResponseDTO findById(UUID id);

    List<ArtistResponseDTO> findAll();

    List<ArtistResponseDTO> findByName(String name, String sortDirection);

    List<ArtistResponseDTO> findByType(ArtistType type);

    ArtistResponseDTO update(UUID id, ArtistRequestDTO dto);

    void delete(UUID id);
}
