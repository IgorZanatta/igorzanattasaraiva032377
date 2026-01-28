package br.gov.mt.seplag.igorzannattasaraiva032377.entity.artistGenre;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class ArtistGenreId implements Serializable {

    @Column(name = "artist_id", nullable = false)
    private UUID artistId;

    @Column(name = "genre_id", nullable = false)
    private UUID genreId;
}