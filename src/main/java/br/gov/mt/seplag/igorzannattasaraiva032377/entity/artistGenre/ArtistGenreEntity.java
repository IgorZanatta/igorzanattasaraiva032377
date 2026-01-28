package br.gov.mt.seplag.igorzannattasaraiva032377.entity.artistGenre;

import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.genre.GenreEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "artist_genre")
public class ArtistGenreEntity {

    @EmbeddedId
    private ArtistGenreId id;

    @MapsId("artistId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artist_id", nullable = false)
    private ArtistEntity artist;

    @MapsId("genreId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "genre_id", nullable = false)
    private GenreEntity genre;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
    }
}