package br.gov.mt.seplag.igorzannattasaraiva032377.service.albumCover;

import java.io.ByteArrayInputStream;
import java.util.Collections;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.albumCover.response.AlbumCoverResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.album.AlbumEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.albumCover.AlbumCoverEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.exception.ResourceNotFoundException;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.album.AlbumRepository;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.albumCover.AlbumCoverRepository;
import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;

@ExtendWith(MockitoExtension.class)
class AlbumCoverServiceImplTest {

    @Mock
    private AlbumCoverRepository albumCoverRepository;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private MinioClient minioClient;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private AlbumCoverServiceImpl albumCoverService;

    private void setupBucketConfig() {
        ReflectionTestUtils.setField(albumCoverService, "bucket", "test-bucket");
        ReflectionTestUtils.setField(albumCoverService, "publicUrl", "");
    }

    @Test
    void uploadCovers_shouldUploadAndMarkFirstAsPrimaryWhenAlbumHasNoPrimary() throws Exception {
        setupBucketConfig();
        UUID albumId = UUID.randomUUID();
        AlbumEntity album = new AlbumEntity();
        album.setId(albumId);
        album.setTitle("Album Title");

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(albumCoverRepository.findByAlbumIdAndIsPrimaryTrue(albumId)).thenReturn(Optional.empty());

        when(multipartFile.getOriginalFilename()).thenReturn("cover.jpg");
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        when(multipartFile.getSize()).thenReturn(10L);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");

        // MinIO mocks
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class))).thenReturn("http://minio/presigned");

        ArgumentCaptor<AlbumCoverEntity> captor = ArgumentCaptor.forClass(AlbumCoverEntity.class);
        when(albumCoverRepository.save(captor.capture())).thenAnswer(inv -> {
            AlbumCoverEntity e = captor.getValue();
            // simulate generated id
            e.setId(UUID.randomUUID());
            return e;
        });

        List<AlbumCoverResponseDTO> result = albumCoverService.uploadCovers(albumId, Collections.singletonList(multipartFile));

        assertEquals(1, result.size());
        AlbumCoverResponseDTO dto = result.get(0);
        assertNotNull(dto.getId());
        assertEquals(albumId, dto.getAlbumId());
        assertTrue(dto.getIsPrimary());
        assertEquals("http://minio/presigned", dto.getUrl());

        AlbumCoverEntity saved = captor.getValue();
        assertTrue(saved.getIsPrimary());
        assertEquals("image/jpeg", saved.getContentType());
        assertEquals(10L, saved.getFileSize());

        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    void uploadCovers_shouldThrowWhenAlbumNotFound() {
        setupBucketConfig();
        UUID albumId = UUID.randomUUID();
        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> albumCoverService.uploadCovers(albumId, Collections.singletonList(multipartFile)));
    }

    @Test
    void listCoversWithPresignedUrls_shouldReturnMappedDTOs() throws Exception {
        setupBucketConfig();
        UUID albumId = UUID.randomUUID();
        AlbumEntity album = new AlbumEntity();
        album.setId(albumId);

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        AlbumCoverEntity cover = AlbumCoverEntity.builder()
                .id(UUID.randomUUID())
                .album(album)
                .objectKey("albums/Album/cover.jpg")
                .contentType("image/jpeg")
                .fileSize(10L)
                .isPrimary(true)
                .build();

        when(albumCoverRepository.findByAlbumId(albumId)).thenReturn(Collections.singletonList(cover));
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class))).thenReturn("http://minio/presigned2");

        List<AlbumCoverResponseDTO> result = albumCoverService.listCoversWithPresignedUrls(albumId);

        assertEquals(1, result.size());
        AlbumCoverResponseDTO dto = result.get(0);
        assertEquals(cover.getId(), dto.getId());
        assertEquals(albumId, dto.getAlbumId());
        assertEquals("http://minio/presigned2", dto.getUrl());
    }

    @Test
    void listCoversWithPresignedUrls_shouldThrowWhenAlbumNotFound() {
        setupBucketConfig();
        UUID albumId = UUID.randomUUID();
        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> albumCoverService.listCoversWithPresignedUrls(albumId));
    }

    @Test
    void getPrimaryCover_shouldReturnPrimaryCover() throws Exception {
        setupBucketConfig();
        UUID albumId = UUID.randomUUID();
        AlbumEntity album = new AlbumEntity();
        album.setId(albumId);

        AlbumCoverEntity cover = AlbumCoverEntity.builder()
                .id(UUID.randomUUID())
                .album(album)
                .objectKey("albums/Album/primary.jpg")
                .contentType("image/jpeg")
                .fileSize(20L)
                .isPrimary(true)
                .build();

        when(albumCoverRepository.findByAlbumIdAndIsPrimaryTrue(albumId)).thenReturn(Optional.of(cover));
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class))).thenReturn("http://minio/primary");

        AlbumCoverResponseDTO dto = albumCoverService.getPrimaryCover(albumId);

        assertEquals(cover.getId(), dto.getId());
        assertEquals(albumId, dto.getAlbumId());
        assertTrue(dto.getIsPrimary());
        assertEquals("http://minio/primary", dto.getUrl());
    }

    @Test
    void getPrimaryCover_shouldThrowWhenNotFound() {
        setupBucketConfig();
        UUID albumId = UUID.randomUUID();
        when(albumCoverRepository.findByAlbumIdAndIsPrimaryTrue(albumId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> albumCoverService.getPrimaryCover(albumId));
    }
}
