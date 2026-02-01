package br.gov.mt.seplag.igorzannattasaraiva032377.repository.album;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.gov.mt.seplag.igorzannattasaraiva032377.entity.album.AlbumEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistType;

public interface AlbumRepository extends JpaRepository<AlbumEntity, UUID> {

    List<AlbumEntity> findByTitleContainingIgnoreCase(String title);

    List<AlbumEntity> findByReleaseYear(Integer releaseYear);

    Page<AlbumEntity> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<AlbumEntity> findByReleaseYear(Integer releaseYear, Pageable pageable);

    @Query(value = "select distinct aa.album from ArtistAlbumEntity aa where aa.artist.type = :type",
           countQuery = "select count(distinct aa.album) from ArtistAlbumEntity aa where aa.artist.type = :type")
    Page<AlbumEntity> findByArtistType(@Param("type") ArtistType type, Pageable pageable);
}