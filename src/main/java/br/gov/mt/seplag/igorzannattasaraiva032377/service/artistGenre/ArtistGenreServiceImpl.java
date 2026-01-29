package br.gov.mt.seplag.igorzannattasaraiva032377.service.artistGenre;

import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artistGenre.ArtistGenreEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artistGenre.ArtistGenreId;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.genre.GenreEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.exception.ConflictException;
import br.gov.mt.seplag.igorzannattasaraiva032377.exception.ResourceNotFoundException;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.artist.ArtistRepository;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.artistGenre.ArtistGenreRepository;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.genre.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArtistGenreServiceImpl implements ArtistGenreService {

    private final ArtistGenreRepository artistGenreRepository;
    private final ArtistRepository artistRepository;
    private final GenreRepository genreRepository;

    @Override
    public void linkArtistToGenre(UUID artistId, UUID genreId) {
        if (artistGenreRepository.existsByIdArtistIdAndIdGenreId(artistId, genreId)) {
            throw new ConflictException("Relação artista/gênero já existe");
        }

        ArtistEntity artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artista não encontrado"));

        GenreEntity genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Gênero não encontrado"));

        ArtistGenreId id = new ArtistGenreId();
        id.setArtistId(artistId);
        id.setGenreId(genreId);

        ArtistGenreEntity relation = new ArtistGenreEntity();
        relation.setId(id);
        relation.setArtist(artist);
        relation.setGenre(genre);

        artistGenreRepository.save(relation);
    }

    @Override
    public void unlinkArtistFromGenre(UUID artistId, UUID genreId) {
        if (!artistGenreRepository.existsByIdArtistIdAndIdGenreId(artistId, genreId)) {
            return; // ou lançar ResourceNotFoundException se preferir
        }
        artistGenreRepository.deleteByIdArtistIdAndIdGenreId(artistId, genreId);
    }

    @Override
    public List<UUID> getGenreIdsByArtist(UUID artistId) {
        return artistGenreRepository.findByIdArtistId(artistId).stream()
                .map(rel -> rel.getId().getGenreId())
                .toList();
    }

    @Override
    public List<UUID> getArtistIdsByGenre(UUID genreId) {
        return artistGenreRepository.findByIdGenreId(genreId).stream()
                .map(rel -> rel.getId().getArtistId())
                .toList();
    }
}