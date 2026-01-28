package br.gov.mt.seplag.igorzannattasaraiva032377.repository.album;

import br.gov.mt.seplag.igorzannattasaraiva032377.entity.album.AlbumEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AlbumRepository extends JpaRepository<AlbumEntity, UUID> {

    List<AlbumEntity> findByTitleContainingIgnoreCase(String title);

    List<AlbumEntity> findByReleaseYear(Integer releaseYear);

    Page<AlbumEntity> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<AlbumEntity> findByReleaseYear(Integer releaseYear, Pageable pageable);
}