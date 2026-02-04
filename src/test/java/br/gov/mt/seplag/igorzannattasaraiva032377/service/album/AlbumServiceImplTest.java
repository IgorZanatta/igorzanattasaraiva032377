package br.gov.mt.seplag.igorzannattasaraiva032377.service.album;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.album.request.AlbumRequestDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.album.response.AlbumResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.album.AlbumEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistType;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.album.AlbumRepository;

@ExtendWith(MockitoExtension.class)
class AlbumServiceImplTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private AlbumServiceImpl albumService;

    @Test
    void create_shouldPersistAlbumAndSendWebSocketNotification() {
        UUID albumId = UUID.randomUUID();
        AlbumRequestDTO request = new AlbumRequestDTO("Master of Puppets", 1986);

        when(albumRepository.save(any(AlbumEntity.class))).thenAnswer(invocation -> {
            AlbumEntity saved = invocation.getArgument(0, AlbumEntity.class);
            saved.setId(albumId);
            return saved;
        });

        AlbumResponseDTO response = albumService.create(request);

        assertNotNull(response);
        assertEquals(albumId, response.id());
        assertEquals("Master of Puppets", response.title());
        assertEquals(1986, response.releaseYear());

        ArgumentCaptor<AlbumEntity> albumCaptor = ArgumentCaptor.forClass(AlbumEntity.class);
        verify(albumRepository).save(albumCaptor.capture());
        AlbumEntity persisted = albumCaptor.getValue();
        assertEquals("Master of Puppets", persisted.getTitle());
        assertEquals(1986, persisted.getReleaseYear());

        ArgumentCaptor<AlbumResponseDTO> dtoCaptor = ArgumentCaptor.forClass(AlbumResponseDTO.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/albums/new"), dtoCaptor.capture());
        AlbumResponseDTO sentDto = dtoCaptor.getValue();
        assertEquals(albumId, sentDto.id());
        assertEquals("Master of Puppets", sentDto.title());
    }

    @Test
    void findAll_shouldReturnPaginatedAlbumResponses() {
        var pageable = PageRequest.of(0, 2);
        AlbumEntity a1 = AlbumEntity.builder().id(UUID.randomUUID()).title("Album 1").releaseYear(2000).build();
        AlbumEntity a2 = AlbumEntity.builder().id(UUID.randomUUID()).title("Album 2").releaseYear(2001).build();

        Page<AlbumEntity> page = new PageImpl<>(List.of(a1, a2), pageable, 2);
        when(albumRepository.findAll(pageable)).thenReturn(page);

        Page<AlbumResponseDTO> result = albumService.findAll(pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals("Album 1", result.getContent().get(0).title());
        assertEquals("Album 2", result.getContent().get(1).title());

        verify(albumRepository).findAll(pageable);
    }

    @Test
    void findByArtistType_shouldReturnPaginatedAlbumsFilteredByArtistType() {
        var pageable = PageRequest.of(0, 5);
        ArtistType type = ArtistType.BAND;

        AlbumEntity a1 = AlbumEntity.builder().id(UUID.randomUUID()).title("Rock Band Album").releaseYear(1990).build();
        Page<AlbumEntity> page = new PageImpl<>(List.of(a1), pageable, 1);

        when(albumRepository.findByArtistType(type, pageable)).thenReturn(page);

        Page<AlbumResponseDTO> result = albumService.findByArtistType(type, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Rock Band Album", result.getContent().get(0).title());
        verify(albumRepository).findByArtistType(type, pageable);
    }
}
