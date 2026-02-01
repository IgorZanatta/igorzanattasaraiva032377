package br.gov.mt.seplag.igorzannattasaraiva032377.service.albumCover;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.albumCover.response.AlbumCoverResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.album.AlbumEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.albumCover.AlbumCoverEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.exception.ResourceNotFoundException;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.album.AlbumRepository;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.albumCover.AlbumCoverRepository;
import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlbumCoverServiceImpl implements AlbumCoverService {

    private final AlbumCoverRepository albumCoverRepository;
    private final AlbumRepository albumRepository;
    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

        @Value("${minio.public-url:}")
        private String publicUrl;

    private static final int PRESIGNED_EXPIRATION_MINUTES = 30;

    @Override
    @Transactional
    public List<AlbumCoverResponseDTO> uploadCovers(
            UUID albumId,
            List<MultipartFile> files
    ) {

        AlbumEntity album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum não encontrado"));

        boolean albumAlreadyHasPrimary = albumCoverRepository
                .findByAlbumIdAndIsPrimaryTrue(albumId)
                .isPresent();

        boolean markFirstAsPrimary = !albumAlreadyHasPrimary;

        java.util.ArrayList<AlbumCoverResponseDTO> responses = new java.util.ArrayList<>();

        for (MultipartFile file : files) {
            String extension = getExtension(file.getOriginalFilename());

            String objectKey = "albums/%s/%s%s".formatted(
                                        album.getTitle(),
                                        UUID.randomUUID(),
                                        extension
                        );

            uploadToMinio(file, objectKey);

            boolean isPrimary = markFirstAsPrimary;
            markFirstAsPrimary = false;

            AlbumCoverEntity entity = AlbumCoverEntity.builder()
                    .album(album)
                    .objectKey(objectKey)
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .isPrimary(isPrimary)
                    .build();

            AlbumCoverEntity saved = albumCoverRepository.save(entity);

            responses.add(toResponseDTO(saved, generatePresignedUrl(objectKey)));
        }

        return responses;
    }

    @Override
    public List<AlbumCoverResponseDTO> listCoversWithPresignedUrls(UUID albumId) {

        albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum não encontrado"));

        return albumCoverRepository.findByAlbumId(albumId)
                .stream()
                .map(cover ->
                        toResponseDTO(
                                cover,
                                generatePresignedUrl(cover.getObjectKey())
                        )
                )
                .toList();
    }

    @Override
    public AlbumCoverResponseDTO getPrimaryCover(UUID albumId) {

        AlbumCoverEntity cover = albumCoverRepository
                .findByAlbumIdAndIsPrimaryTrue(albumId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Capa primária não encontrada")
                );

        return toResponseDTO(
                cover,
                generatePresignedUrl(cover.getObjectKey())
        );
    }

    // ========================
    // Métodos auxiliares
    // ========================

    private void uploadToMinio(MultipartFile file, String objectKey) {
        try {
            ensureBucketExists();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .stream(
                                    file.getInputStream(),
                                    file.getSize(),
                                    -1
                            )
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Erro ao fazer upload da capa no MinIO", e);
        }
    }

        private void ensureBucketExists() {
                try {
                        boolean exists = minioClient.bucketExists(
                                        BucketExistsArgs.builder()
                                                        .bucket(bucket)
                                                        .build()
                        );

                        if (!exists) {
                                minioClient.makeBucket(
                                                MakeBucketArgs.builder()
                                                                .bucket(bucket)
                                                                .build()
                                );
                        }
                } catch (Exception e) {
                        throw new RuntimeException("Erro ao validar/criar bucket no MinIO", e);
                }
        }

    private String generatePresignedUrl(String objectKey) {
        try {
                        String presignedUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .method(Method.GET)
                            .expiry(PRESIGNED_EXPIRATION_MINUTES, TimeUnit.MINUTES)
                            .build()
            );

                        return applyPublicUrlIfConfigured(presignedUrl);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar URL pré-assinada", e);
        }
    }

        private String applyPublicUrlIfConfigured(String presignedUrl) {
                if (publicUrl == null || publicUrl.isBlank()) {
                        return presignedUrl;
                }

                try {
                        URI original = URI.create(presignedUrl);

                        String base = publicUrl.endsWith("/")
                                        ? publicUrl.substring(0, publicUrl.length() - 1)
                                        : publicUrl;

                        StringBuilder sb = new StringBuilder(base);
                        sb.append(original.getRawPath());

                        if (original.getRawQuery() != null && !original.getRawQuery().isEmpty()) {
                                sb.append("?").append(original.getRawQuery());
                        }

                        return sb.toString();
                } catch (Exception e) {
                        return presignedUrl;
                }
        }

    private AlbumCoverResponseDTO toResponseDTO(
            AlbumCoverEntity entity,
            String url
    ) {
        return AlbumCoverResponseDTO.builder()
                .id(entity.getId())
                .albumId(entity.getAlbum().getId())
                .objectKey(entity.getObjectKey())
                .contentType(entity.getContentType())
                .fileSize(entity.getFileSize())
                .isPrimary(entity.getIsPrimary())
                .url(url)
                .build();
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }
}
