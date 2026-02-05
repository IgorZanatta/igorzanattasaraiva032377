package br.gov.mt.seplag.igorzannattasaraiva032377.repository.artist;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistType;

public interface ArtistRepository extends JpaRepository<ArtistEntity, UUID> {
    List<ArtistEntity> findByType(ArtistType type);
    
    @Query(value = "SELECT * FROM artist WHERE LOWER(unaccent(name)) LIKE LOWER(unaccent(CONCAT('%', :name, '%')))", nativeQuery = true)
    List<ArtistEntity> findByNameContainingIgnoreCase(@Param("name") String name);
}