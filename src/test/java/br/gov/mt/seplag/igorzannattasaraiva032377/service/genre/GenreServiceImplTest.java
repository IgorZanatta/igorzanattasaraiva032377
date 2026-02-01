package br.gov.mt.seplag.igorzannattasaraiva032377.service.genre;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.genre.request.GenreRequestDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.genre.response.GenreResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.genre.GenreEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.exception.ResourceNotFoundException;
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
    void findById_shouldReturnResponseWhenFound() {
        UUID id = UUID.randomUUID();
        GenreEntity entity = new GenreEntity();
        entity.setId(id);
        entity.setName("Metal");
        entity.setActive(true);
        when(genreRepository.findById(id)).thenReturn(Optional.of(entity));

        GenreResponseDTO response = genreService.findById(id);
        assertEquals(id, response.id());
        assertEquals("Metal", response.name());
    }

    @Test
    void findById_shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(genreRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> genreService.findById(id));
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

    @Test
    void update_shouldApplyChangesAndReturnResponse() {
        UUID id = UUID.randomUUID();
        GenreEntity existing = new GenreEntity();
        existing.setId(id);
        existing.setName("Old");
        existing.setActive(true);

        when(genreRepository.findById(id)).thenReturn(Optional.of(existing));
        when(genreRepository.save(any(GenreEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        GenreRequestDTO request = new GenreRequestDTO("New", false);

        GenreResponseDTO response = genreService.update(id, request);

        assertEquals(id, response.id());
        assertEquals("New", response.name());
        assertFalse(response.active());
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(genreRepository.findById(id)).thenReturn(Optional.empty());

        GenreRequestDTO request = new GenreRequestDTO("New", true);
        assertThrows(ResourceNotFoundException.class, () -> genreService.update(id, request));
    }

    @Test
    void deactivate_shouldSetActiveFalse() {
        UUID id = UUID.randomUUID();
        GenreEntity existing = new GenreEntity();
        existing.setId(id);
        existing.setName("Genre");
        existing.setActive(true);
        when(genreRepository.findById(id)).thenReturn(Optional.of(existing));

        genreService.deactivate(id);

        assertFalse(existing.isActive());
        verify(genreRepository).save(existing);
    }

    @Test
    void deactivate_shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(genreRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> genreService.deactivate(id));
    }
}
