package br.gov.mt.seplag.igorzannattasaraiva032377.service.artist;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.artist.request.ArtistRequestDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.artist.response.ArtistResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistType;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.genre.GenreEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.exception.ResourceNotFoundException;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.artist.ArtistRepository;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.genre.GenreRepository;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.artistGenre.ArtistGenreService;

@ExtendWith(MockitoExtension.class)
class ArtistServiceImplTest {

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private ArtistGenreService artistGenreService;

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private ArtistServiceImpl artistService;

    @Test
    void create_shouldPersistArtistAndLinkGenresAndReturnResponse() {
        UUID artistId = UUID.randomUUID();
        UUID rockId = UUID.randomUUID();
        UUID popId = UUID.randomUUID();

        ArtistRequestDTO dto = new ArtistRequestDTO(
                "John Doe",
                ArtistType.SOLO,
                List.of("Rock", "Pop")
        );

        when(artistRepository.save(any(ArtistEntity.class))).thenAnswer(invocation -> {
            ArtistEntity saved = invocation.getArgument(0, ArtistEntity.class);
            saved.setId(artistId);
            return saved;
        });

        when(genreRepository.findByName("Rock"))
                .thenReturn(Optional.of(GenreEntity.builder().id(rockId).name("Rock").build()));
        when(genreRepository.findByName("Pop"))
                .thenReturn(Optional.of(GenreEntity.builder().id(popId).name("Pop").build()));

        when(artistGenreService.getGenreIdsByArtist(artistId))
                .thenReturn(List.of(rockId, popId));
        when(genreRepository.findById(rockId))
                .thenReturn(Optional.of(GenreEntity.builder().id(rockId).name("Rock").build()));
        when(genreRepository.findById(popId))
                .thenReturn(Optional.of(GenreEntity.builder().id(popId).name("Pop").build()));

        ArtistResponseDTO response = artistService.create(dto);

        assertNotNull(response);
        assertEquals(artistId, response.id());
        assertEquals("John Doe", response.name());
        assertEquals(ArtistType.SOLO, response.type());
        assertTrue(response.genres().containsAll(List.of("Rock", "Pop")));
        assertEquals(2, response.genres().size());

        ArgumentCaptor<ArtistEntity> artistCaptor = ArgumentCaptor.forClass(ArtistEntity.class);
        verify(artistRepository).save(artistCaptor.capture());
        ArtistEntity persisted = artistCaptor.getValue();
        assertEquals("John Doe", persisted.getName());
        assertEquals(ArtistType.SOLO, persisted.getType());

        verify(artistGenreService).linkArtistToGenre(artistId, rockId);
        verify(artistGenreService).linkArtistToGenre(artistId, popId);
    }

    @Test
    void findAll_shouldSearchByNameAndReturnMappedResponses() {
        UUID id = UUID.randomUUID();
        ArtistEntity entity = ArtistEntity.builder()
                .id(id)
                .name("Metallica")
                .type(ArtistType.BAND)
                .build();

        when(artistRepository.findByNameContainingIgnoreCase("metal"))
                .thenReturn(List.of(entity));
        when(artistGenreService.getGenreIdsByArtist(id)).thenReturn(List.of());

        var result = artistService.findAll("metal", null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        ArtistResponseDTO dto = result.get(0);
        assertEquals(id, dto.id());
        assertEquals("Metallica", dto.name());
        assertEquals(ArtistType.BAND, dto.type());
        assertTrue(dto.genres().isEmpty());

        verify(artistRepository).findByNameContainingIgnoreCase("metal");
    }

    @Test
    void findAll_shouldSortAlphabeticallyAscendingWhenSortDirectionIsNull() {
        ArtistEntity a = ArtistEntity.builder().id(UUID.randomUUID()).name("Charlie").type(ArtistType.SOLO).build();
        ArtistEntity b = ArtistEntity.builder().id(UUID.randomUUID()).name("alice").type(ArtistType.SOLO).build();
        ArtistEntity c = ArtistEntity.builder().id(UUID.randomUUID()).name("Bob").type(ArtistType.SOLO).build();

        when(artistRepository.findByNameContainingIgnoreCase("a"))
                .thenReturn(List.of(a, b, c));
        when(artistGenreService.getGenreIdsByArtist(any())).thenReturn(List.of());

        var result = artistService.findAll("a", null, null);

        assertEquals(3, result.size());
        assertEquals("alice", result.get(0).name());
        assertEquals("Bob", result.get(1).name());
        assertEquals("Charlie", result.get(2).name());
    }

    @Test
        void findAll_shouldSortAlphabeticallyDescendingWhenSortDirectionIsDesc() {
        ArtistEntity a = ArtistEntity.builder().id(UUID.randomUUID()).name("Charlie").type(ArtistType.SOLO).build();
        ArtistEntity b = ArtistEntity.builder().id(UUID.randomUUID()).name("alice").type(ArtistType.SOLO).build();
        ArtistEntity c = ArtistEntity.builder().id(UUID.randomUUID()).name("Bob").type(ArtistType.SOLO).build();

        when(artistRepository.findByNameContainingIgnoreCase("a"))
                .thenReturn(List.of(a, b, c));
        when(artistGenreService.getGenreIdsByArtist(any())).thenReturn(List.of());

        var result = artistService.findAll("a", null, "DESC");

        assertEquals(3, result.size());
        assertEquals("Charlie", result.get(0).name());
        assertEquals("Bob", result.get(1).name());
        assertEquals("alice", result.get(2).name());
    }

    @Test
    void update_shouldUpdateArtistAndGenres() {
        UUID artistId = UUID.randomUUID();
        UUID rockId = UUID.randomUUID();
        UUID metalId = UUID.randomUUID();

        ArtistRequestDTO dto = new ArtistRequestDTO(
                "Updated Name",
                ArtistType.BAND,
                List.of("Rock", "Metal")
        );

        ArtistEntity existing = ArtistEntity.builder()
                .id(artistId)
                .name("Old Name")
                .type(ArtistType.SOLO)
                .build();

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(existing));
        when(artistRepository.save(any(ArtistEntity.class))).thenReturn(existing);

        when(genreRepository.findByName("Rock"))
                .thenReturn(Optional.of(GenreEntity.builder().id(rockId).name("Rock").build()));
        when(genreRepository.findByName("Metal"))
                .thenReturn(Optional.of(GenreEntity.builder().id(metalId).name("Metal").build()));

        when(artistGenreService.getGenreIdsByArtist(artistId))
                .thenReturn(List.of(rockId, metalId));
        when(genreRepository.findById(rockId))
                .thenReturn(Optional.of(GenreEntity.builder().id(rockId).name("Rock").build()));
        when(genreRepository.findById(metalId))
                .thenReturn(Optional.of(GenreEntity.builder().id(metalId).name("Metal").build()));

        ArtistResponseDTO response = artistService.update(artistId, dto);

        assertNotNull(response);
        assertEquals(artistId, response.id());
        assertEquals("Updated Name", response.name());
        assertEquals(ArtistType.BAND, response.type());
        assertTrue(response.genres().containsAll(List.of("Rock", "Metal")));

        verify(artistRepository).findById(artistId);
        verify(artistRepository).save(existing);
    }

    @Test
    void update_shouldThrowWhenArtistNotFound() {
        UUID artistId = UUID.randomUUID();
        ArtistRequestDTO dto = new ArtistRequestDTO("Name", ArtistType.SOLO, List.of());

        when(artistRepository.findById(artistId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> artistService.update(artistId, dto));
    }

    @Test
    void create_shouldThrowWhenGenreNotFound() {
        ArtistRequestDTO dto = new ArtistRequestDTO(
                "Artist Name",
                ArtistType.SOLO,
                List.of("NonExistentGenre")
        );

        UUID artistId = UUID.randomUUID();
        when(artistRepository.save(any(ArtistEntity.class))).thenAnswer(invocation -> {
            ArtistEntity saved = invocation.getArgument(0, ArtistEntity.class);
            saved.setId(artistId);
            return saved;
        });

        when(genreRepository.findByName("NonExistentGenre")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> artistService.create(dto));
    }
}
