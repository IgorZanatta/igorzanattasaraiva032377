package br.gov.mt.seplag.igorzannattasaraiva032377.service.artistAlbum;

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

import br.gov.mt.seplag.igorzannattasaraiva032377.entity.album.AlbumEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artistAlbum.ArtistAlbumEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artistAlbum.ArtistAlbumId;
import br.gov.mt.seplag.igorzannattasaraiva032377.exception.ConflictException;
import br.gov.mt.seplag.igorzannattasaraiva032377.exception.ResourceNotFoundException;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.album.AlbumRepository;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.artist.ArtistRepository;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.artistAlbum.ArtistAlbumRepository;

@ExtendWith(MockitoExtension.class)
class ArtistAlbumServiceImplTest {

    @Mock
    private ArtistAlbumRepository artistAlbumRepository;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private AlbumRepository albumRepository;

    @InjectMocks
    private ArtistAlbumServiceImpl artistAlbumService;

    @Test
    void linkArtistToAlbum_shouldPersistRelationWhenNotExists() {
        UUID artistId = UUID.randomUUID();
        UUID albumId = UUID.randomUUID();

        when(artistAlbumRepository.existsByIdArtistIdAndIdAlbumId(artistId, albumId)).thenReturn(false);
        when(artistRepository.findById(artistId)).thenReturn(Optional.of(new ArtistEntity()));
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(new AlbumEntity()));

        artistAlbumService.linkArtistToAlbum(artistId, albumId);

        ArgumentCaptor<ArtistAlbumEntity> captor = ArgumentCaptor.forClass(ArtistAlbumEntity.class);
        verify(artistAlbumRepository).save(captor.capture());
        ArtistAlbumEntity saved = captor.getValue();

        assertNotNull(saved.getId());
        assertEquals(artistId, saved.getId().getArtistId());
        assertEquals(albumId, saved.getId().getAlbumId());
        assertNotNull(saved.getArtist());
        assertNotNull(saved.getAlbum());
    }

    @Test
    void linkArtistToAlbum_shouldThrowConflictWhenAlreadyExists() {
        UUID artistId = UUID.randomUUID();
        UUID albumId = UUID.randomUUID();

        when(artistAlbumRepository.existsByIdArtistIdAndIdAlbumId(artistId, albumId)).thenReturn(true);

        assertThrows(ConflictException.class, () -> artistAlbumService.linkArtistToAlbum(artistId, albumId));
        verify(artistRepository, never()).findById(any());
        verify(albumRepository, never()).findById(any());
    }

    @Test
    void linkArtistToAlbum_shouldThrowWhenArtistNotFound() {
        UUID artistId = UUID.randomUUID();
        UUID albumId = UUID.randomUUID();

        when(artistAlbumRepository.existsByIdArtistIdAndIdAlbumId(artistId, albumId)).thenReturn(false);
        when(artistRepository.findById(artistId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> artistAlbumService.linkArtistToAlbum(artistId, albumId));
        verify(albumRepository, never()).findById(any());
    }

    @Test
    void linkArtistToAlbum_shouldThrowWhenAlbumNotFound() {
        UUID artistId = UUID.randomUUID();
        UUID albumId = UUID.randomUUID();

        when(artistAlbumRepository.existsByIdArtistIdAndIdAlbumId(artistId, albumId)).thenReturn(false);
        when(artistRepository.findById(artistId)).thenReturn(Optional.of(new ArtistEntity()));
        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> artistAlbumService.linkArtistToAlbum(artistId, albumId));
    }

    @Test
    void unlinkArtistFromAlbum_shouldDeleteWhenExists() {
        UUID artistId = UUID.randomUUID();
        UUID albumId = UUID.randomUUID();

        when(artistAlbumRepository.existsByIdArtistIdAndIdAlbumId(artistId, albumId)).thenReturn(true);

        artistAlbumService.unlinkArtistFromAlbum(artistId, albumId);

        verify(artistAlbumRepository).deleteByIdArtistIdAndIdAlbumId(artistId, albumId);
    }

    @Test
    void unlinkArtistFromAlbum_shouldDoNothingWhenNotExists() {
        UUID artistId = UUID.randomUUID();
        UUID albumId = UUID.randomUUID();

        when(artistAlbumRepository.existsByIdArtistIdAndIdAlbumId(artistId, albumId)).thenReturn(false);

        artistAlbumService.unlinkArtistFromAlbum(artistId, albumId);

        verify(artistAlbumRepository, never()).deleteByIdArtistIdAndIdAlbumId(any(), any());
    }

    @Test
    void getAlbumIdsByArtist_shouldReturnIdsFromRelations() {
        UUID artistId = UUID.randomUUID();
        UUID albumId = UUID.randomUUID();

        ArtistAlbumId id = new ArtistAlbumId();
        id.setArtistId(artistId);
        id.setAlbumId(albumId);

        ArtistAlbumEntity rel = new ArtistAlbumEntity();
        rel.setId(id);

        when(artistAlbumRepository.findByIdArtistId(artistId)).thenReturn(Collections.singletonList(rel));

        List<UUID> result = artistAlbumService.getAlbumIdsByArtist(artistId);
        assertEquals(1, result.size());
        assertEquals(albumId, result.get(0));
    }

    @Test
    void getArtistIdsByAlbum_shouldReturnIdsFromRelations() {
        UUID artistId = UUID.randomUUID();
        UUID albumId = UUID.randomUUID();

        ArtistAlbumId id = new ArtistAlbumId();
        id.setArtistId(artistId);
        id.setAlbumId(albumId);

        ArtistAlbumEntity rel = new ArtistAlbumEntity();
        rel.setId(id);

        when(artistAlbumRepository.findByIdAlbumId(albumId)).thenReturn(Collections.singletonList(rel));

        List<UUID> result = artistAlbumService.getArtistIdsByAlbum(albumId);
        assertEquals(1, result.size());
        assertEquals(artistId, result.get(0));
    }
}
