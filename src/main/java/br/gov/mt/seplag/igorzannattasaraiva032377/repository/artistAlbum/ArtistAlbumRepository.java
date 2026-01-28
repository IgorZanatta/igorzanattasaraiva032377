package br.gov.mt.seplag.igorzannattasaraiva032377.repository.artistAlbum;


import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artistAlbum.ArtistAlbumEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artistAlbum.ArtistAlbumId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ArtistAlbumRepository extends JpaRepository<ArtistAlbumEntity, ArtistAlbumId> {

    List<ArtistAlbumEntity> findByIdArtistId(UUID artistId);

    List<ArtistAlbumEntity> findByIdAlbumId(UUID albumId);

    boolean existsByIdArtistIdAndIdAlbumId(UUID artistId, UUID albumId);

    void deleteByIdArtistIdAndIdAlbumId(UUID artistId, UUID albumId);
}
