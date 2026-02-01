package br.gov.mt.seplag.igorzannattasaraiva032377.service.artistGenre;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artistGenre.ArtistGenreEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artistGenre.ArtistGenreId;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.genre.GenreEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.exception.ConflictException;
import br.gov.mt.seplag.igorzannattasaraiva032377.exception.ResourceNotFoundException;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.artist.ArtistRepository;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.artistGenre.ArtistGenreRepository;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.genre.GenreRepository;

@ExtendWith(MockitoExtension.class)
class ArtistGenreServiceImplTest {

    @Mock
    private ArtistGenreRepository artistGenreRepository;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private ArtistGenreServiceImpl artistGenreService;

    @Test
    void linkArtistToGenre_shouldPersistRelationWhenNotExists() {
        UUID artistId = UUID.randomUUID();
        UUID genreId = UUID.randomUUID();

        when(artistGenreRepository.existsByIdArtistIdAndIdGenreId(artistId, genreId)).thenReturn(false);
        when(artistRepository.findById(artistId)).thenReturn(Optional.of(new ArtistEntity()));
        when(genreRepository.findById(genreId)).thenReturn(Optional.of(new GenreEntity()));

        artistGenreService.linkArtistToGenre(artistId, genreId);

        ArgumentCaptor<ArtistGenreEntity> captor = ArgumentCaptor.forClass(ArtistGenreEntity.class);
        verify(artistGenreRepository).save(captor.capture());
        ArtistGenreEntity saved = captor.getValue();

        assertNotNull(saved.getId());
        assertEquals(artistId, saved.getId().getArtistId());
        assertEquals(genreId, saved.getId().getGenreId());
        assertNotNull(saved.getArtist());
        assertNotNull(saved.getGenre());
    }

    @Test
    void linkArtistToGenre_shouldThrowConflictWhenAlreadyExists() {
        UUID artistId = UUID.randomUUID();
        UUID genreId = UUID.randomUUID();

        when(artistGenreRepository.existsByIdArtistIdAndIdGenreId(artistId, genreId)).thenReturn(true);

        assertThrows(ConflictException.class, () -> artistGenreService.linkArtistToGenre(artistId, genreId));
        verify(artistRepository, never()).findById(any());
        verify(genreRepository, never()).findById(any());
    }

    @Test
    void linkArtistToGenre_shouldThrowWhenArtistNotFound() {
        UUID artistId = UUID.randomUUID();
        UUID genreId = UUID.randomUUID();

        when(artistGenreRepository.existsByIdArtistIdAndIdGenreId(artistId, genreId)).thenReturn(false);
        when(artistRepository.findById(artistId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> artistGenreService.linkArtistToGenre(artistId, genreId));
        verify(genreRepository, never()).findById(any());
    }

    @Test
    void linkArtistToGenre_shouldThrowWhenGenreNotFound() {
        UUID artistId = UUID.randomUUID();
        UUID genreId = UUID.randomUUID();

        when(artistGenreRepository.existsByIdArtistIdAndIdGenreId(artistId, genreId)).thenReturn(false);
        when(artistRepository.findById(artistId)).thenReturn(Optional.of(new ArtistEntity()));
        when(genreRepository.findById(genreId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> artistGenreService.linkArtistToGenre(artistId, genreId));
    }

    @Test
    void unlinkArtistFromGenre_shouldDeleteWhenExists() {
        UUID artistId = UUID.randomUUID();
        UUID genreId = UUID.randomUUID();

        when(artistGenreRepository.existsByIdArtistIdAndIdGenreId(artistId, genreId)).thenReturn(true);

        artistGenreService.unlinkArtistFromGenre(artistId, genreId);

        verify(artistGenreRepository).deleteByIdArtistIdAndIdGenreId(artistId, genreId);
    }

    @Test
    void unlinkArtistFromGenre_shouldDoNothingWhenNotExists() {
        UUID artistId = UUID.randomUUID();
        UUID genreId = UUID.randomUUID();

        when(artistGenreRepository.existsByIdArtistIdAndIdGenreId(artistId, genreId)).thenReturn(false);

        artistGenreService.unlinkArtistFromGenre(artistId, genreId);

        verify(artistGenreRepository, never()).deleteByIdArtistIdAndIdGenreId(any(), any());
    }

    @Test
    void getGenreIdsByArtist_shouldReturnIdsFromRelations() {
        UUID artistId = UUID.randomUUID();
        UUID genreId = UUID.randomUUID();

        ArtistGenreId id = new ArtistGenreId();
        id.setArtistId(artistId);
        id.setGenreId(genreId);

        ArtistGenreEntity rel = new ArtistGenreEntity();
        rel.setId(id);

        when(artistGenreRepository.findByIdArtistId(artistId)).thenReturn(Collections.singletonList(rel));

        List<UUID> result = artistGenreService.getGenreIdsByArtist(artistId);
        assertEquals(1, result.size());
        assertEquals(genreId, result.get(0));
    }

    @Test
    void getArtistIdsByGenre_shouldReturnIdsFromRelations() {
        UUID artistId = UUID.randomUUID();
        UUID genreId = UUID.randomUUID();

        ArtistGenreId id = new ArtistGenreId();
        id.setArtistId(artistId);
        id.setGenreId(genreId);

        ArtistGenreEntity rel = new ArtistGenreEntity();
        rel.setId(id);

        when(artistGenreRepository.findByIdGenreId(genreId)).thenReturn(Collections.singletonList(rel));

        List<UUID> result = artistGenreService.getArtistIdsByGenre(genreId);
        assertEquals(1, result.size());
        assertEquals(artistId, result.get(0));
    }
}
