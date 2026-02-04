package br.gov.mt.seplag.igorzannattasaraiva032377.service.genre;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.genre.request.GenreRequestDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.genre.response.GenreResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.genre.GenreEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.mapper.genre.GenreMapper;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.genre.GenreRepository;

@ExtendWith(MockitoExtension.class)
class GenreServiceImplTest {

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private GenreServiceImpl genreService;

    @Test
    void create_shouldPersistAndReturnResponse() {
        GenreRequestDTO request = new GenreRequestDTO("Rock", true);
        GenreEntity entity = GenreMapper.toEntity(request);
        GenreEntity saved = new GenreEntity();
        saved.setId(UUID.randomUUID());
        saved.setName("Rock");
        saved.setActive(true);

        when(genreRepository.save(any(GenreEntity.class))).thenReturn(saved);

        GenreResponseDTO response = genreService.create(request);

        assertEquals(saved.getId(), response.id());
        assertEquals("Rock", response.name());
        assertTrue(response.active());
    }

    @Test
    void findAll_shouldReturnSortedResponses() {
        GenreEntity g1 = new GenreEntity();
        g1.setId(UUID.randomUUID());
        g1.setName("rock");
        g1.setActive(true);
        GenreEntity g2 = new GenreEntity();
        g2.setId(UUID.randomUUID());
        g2.setName("Blues");
        g2.setActive(true);
        GenreEntity g3 = new GenreEntity();
        g3.setId(UUID.randomUUID());
        g3.setName("Jazz");
        g3.setActive(true);

        when(genreRepository.findAll()).thenReturn(Arrays.asList(g1, g2, g3));

        List<GenreResponseDTO> result = genreService.findAll();
        List<String> names = result.stream().map(GenreResponseDTO::name).toList();
        assertEquals(Arrays.asList("Blues", "Jazz", "rock"), names);
    }

    @Test
    void findByName_shouldFilterAndSortAscendingByDefault() {
        GenreEntity g1 = new GenreEntity();
        g1.setId(UUID.randomUUID());
        g1.setName("rock");
        GenreEntity g2 = new GenreEntity();
        g2.setId(UUID.randomUUID());
        g2.setName("Metal");

        when(genreRepository.findByNameContainingIgnoreCase("o"))
                .thenReturn(Arrays.asList(g1, g2));

        List<GenreResponseDTO> result = genreService.findByName("o", null);
        List<String> names = result.stream().map(GenreResponseDTO::name).toList();
        assertEquals(Arrays.asList("Metal", "rock"), names);
    }

    @Test
    void findByName_shouldSortDescendingWhenDirectionIsDesc() {
        GenreEntity g1 = new GenreEntity();
        g1.setId(UUID.randomUUID());
        g1.setName("rock");
        GenreEntity g2 = new GenreEntity();
        g2.setId(UUID.randomUUID());
        g2.setName("Metal");

        when(genreRepository.findByNameContainingIgnoreCase("o"))
                .thenReturn(Arrays.asList(g1, g2));

        List<GenreResponseDTO> result = genreService.findByName("o", "desc");
        List<String> names = result.stream().map(GenreResponseDTO::name).toList();
        assertEquals(Arrays.asList("rock", "Metal"), names);
    }
}
