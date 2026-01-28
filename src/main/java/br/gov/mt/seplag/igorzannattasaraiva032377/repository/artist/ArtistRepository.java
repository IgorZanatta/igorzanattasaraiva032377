package br.gov.mt.seplag.igorzannattasaraiva032377.repository.artist;

import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ArtistRepository extends JpaRepository<ArtistEntity, UUID> {
    List<ArtistEntity> findByType(ArtistType type);
    List<ArtistEntity> findByNameContainingIgnoreCase(String name);
}