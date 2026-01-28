package br.gov.mt.seplag.igorzannattasaraiva032377.entity.artistAlbum;

import br.gov.mt.seplag.igorzannattasaraiva032377.entity.album.AlbumEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "artist_album")
public class ArtistAlbumEntity {

    @EmbeddedId
    private ArtistAlbumId id;

    @MapsId("artistId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artist_id", nullable = false)
    private ArtistEntity artist;

    @MapsId("albumId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "album_id", nullable = false)
    private AlbumEntity album;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
    }
}