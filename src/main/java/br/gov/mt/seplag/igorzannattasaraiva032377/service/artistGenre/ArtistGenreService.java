package br.gov.mt.seplag.igorzannattasaraiva032377.service.artistGenre;

import java.util.List;
import java.util.UUID;

public interface ArtistGenreService {

    void linkArtistToGenre(UUID artistId, UUID genreId);

    void unlinkArtistFromGenre(UUID artistId, UUID genreId);

    List<UUID> getGenreIdsByArtist(UUID artistId);

    List<UUID> getArtistIdsByGenre(UUID genreId);
}