package br.gov.mt.seplag.igorzannattasaraiva032377.repository.genre;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.gov.mt.seplag.igorzannattasaraiva032377.entity.genre.GenreEntity;

public interface GenreRepository extends JpaRepository<GenreEntity, UUID> {

    Optional<GenreEntity> findByName(String name);

    boolean existsByName(String name);

    List<GenreEntity> findByNameContainingIgnoreCase(String name);
}