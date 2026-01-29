package br.gov.mt.seplag.igorzannattasaraiva032377.service.artistAlbum;

import br.gov.mt.seplag.igorzannattasaraiva032377.entity.album.AlbumEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artistAlbum.ArtistAlbumEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artistAlbum.ArtistAlbumId;
import br.gov.mt.seplag.igorzannattasaraiva032377.exception.ConflictException;
import br.gov.mt.seplag.igorzannattasaraiva032377.exception.ResourceNotFoundException;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.album.AlbumRepository;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.artist.ArtistRepository;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.artistAlbum.ArtistAlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArtistAlbumServiceImpl implements ArtistAlbumService {

    private final ArtistAlbumRepository artistAlbumRepository;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;

    @Override
    public void linkArtistToAlbum(UUID artistId, UUID albumId) {
        if (artistAlbumRepository.existsByIdArtistIdAndIdAlbumId(artistId, albumId)) {
            throw new ConflictException("Relação artista/álbum já existe");
        }

        ArtistEntity artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artista não encontrado"));

        AlbumEntity album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum não encontrado"));

        ArtistAlbumId id = new ArtistAlbumId();
        id.setArtistId(artistId);
        id.setAlbumId(albumId);

        ArtistAlbumEntity relation = new ArtistAlbumEntity();
        relation.setId(id);
        relation.setArtist(artist);
        relation.setAlbum(album);

        artistAlbumRepository.save(relation);
    }

    @Override
    public void unlinkArtistFromAlbum(UUID artistId, UUID albumId) {
        if (!artistAlbumRepository.existsByIdArtistIdAndIdAlbumId(artistId, albumId)) {
            return; // ou lançar ResourceNotFoundException se preferir
        }
        artistAlbumRepository.deleteByIdArtistIdAndIdAlbumId(artistId, albumId);
    }

    @Override
    public List<UUID> getAlbumIdsByArtist(UUID artistId) {
        return artistAlbumRepository.findByIdArtistId(artistId).stream()
                .map(rel -> rel.getId().getAlbumId())
                .toList();
    }

    @Override
    public List<UUID> getArtistIdsByAlbum(UUID albumId) {
        return artistAlbumRepository.findByIdAlbumId(albumId).stream()
                .map(rel -> rel.getId().getArtistId())
                .toList();
    }
}