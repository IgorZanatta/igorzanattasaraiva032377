package br.gov.mt.seplag.igorzannattasaraiva032377.repository.artistGenre;


import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artistGenre.ArtistGenreEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artistGenre.ArtistGenreId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ArtistGenreRepository extends JpaRepository<ArtistGenreEntity, ArtistGenreId> {

    List<ArtistGenreEntity> findByIdArtistId(UUID artistId);

    List<ArtistGenreEntity> findByIdGenreId(UUID genreId);

    boolean existsByIdArtistIdAndIdGenreId(UUID artistId, UUID genreId);

    void deleteByIdArtistIdAndIdGenreId(UUID artistId, UUID genreId);
}