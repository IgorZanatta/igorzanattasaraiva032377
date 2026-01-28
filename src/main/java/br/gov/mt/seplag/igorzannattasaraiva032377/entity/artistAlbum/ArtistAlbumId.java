package br.gov.mt.seplag.igorzannattasaraiva032377.entity.artistAlbum;


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
public class ArtistAlbumId implements Serializable {

    @Column(name = "artist_id", nullable = false)
    private UUID artistId;

    @Column(name = "album_id", nullable = false)
    private UUID albumId;
}