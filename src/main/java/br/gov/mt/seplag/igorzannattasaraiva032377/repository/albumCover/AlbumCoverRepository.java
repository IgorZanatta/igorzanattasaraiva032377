package br.gov.mt.seplag.igorzannattasaraiva032377.repository.albumCover;

import br.gov.mt.seplag.igorzannattasaraiva032377.entity.albumCover.AlbumCoverEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlbumCoverRepository extends JpaRepository<AlbumCoverEntity, UUID> {

    List<AlbumCoverEntity> findByAlbumId(UUID albumId);

    Optional<AlbumCoverEntity> findByAlbumIdAndIsPrimaryTrue(UUID albumId);

    boolean existsByAlbumIdAndObjectKey(UUID albumId, String objectKey);
}
