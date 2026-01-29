package br.gov.mt.seplag.igorzannattasaraiva032377.service.artistAlbum;


import java.util.List;
import java.util.UUID;

public interface ArtistAlbumService {

    void linkArtistToAlbum(UUID artistId, UUID albumId);

    void unlinkArtistFromAlbum(UUID artistId, UUID albumId);

    List<UUID> getAlbumIdsByArtist(UUID artistId);

    List<UUID> getArtistIdsByAlbum(UUID albumId);
}