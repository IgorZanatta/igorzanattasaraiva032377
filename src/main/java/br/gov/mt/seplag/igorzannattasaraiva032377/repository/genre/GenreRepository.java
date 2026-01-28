package br.gov.mt.seplag.igorzannattasaraiva032377.repository.genre;

import br.gov.mt.seplag.igorzannattasaraiva032377.entity.genre.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GenreRepository extends JpaRepository<GenreEntity, UUID> {
    Optional<GenreEntity> findByName(String name);
    boolean existsByName(String name);
}