package br.gov.mt.seplag.igorzannattasaraiva032377.controller.artist;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.album.response.AlbumResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.artist.request.ArtistRequestDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.artist.response.ArtistResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistType;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.album.AlbumService;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.artist.ArtistService;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.artistAlbum.ArtistAlbumService;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.artistGenre.ArtistGenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService service;
    private final ArtistAlbumService artistAlbumService;
    private final ArtistGenreService artistGenreService;
    private final AlbumService albumService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArtistResponseDTO create(@RequestBody @Valid ArtistRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping("/{id}")
    public ArtistResponseDTO findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @GetMapping
    public List<ArtistResponseDTO> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) ArtistType type,
            @RequestParam(required = false, defaultValue = "asc") String sort
    ) {
        if (name != null) {
            return service.findByName(name, sort);
        }
        if (type != null) {
            return service.findByType(type);
        }
        return service.findAll();
    }

    @PutMapping("/{id}")
    public ArtistResponseDTO update(
            @PathVariable UUID id,
            @RequestBody @Valid ArtistRequestDTO dto
    ) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }


    @GetMapping("/{artistId}/albums")
    public List<AlbumResponseDTO> getAlbumsByArtist(@PathVariable UUID artistId) {
        var albumIds = artistAlbumService.getAlbumIdsByArtist(artistId);
        return albumIds.stream()
                .map(albumService::findById)
                .toList();
    }


    @PostMapping("/{artistId}/albums/{albumId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addAlbumToArtist(
            @PathVariable UUID artistId,
            @PathVariable UUID albumId
    ) {
        artistAlbumService.linkArtistToAlbum(artistId, albumId);
    }

    @DeleteMapping("/{artistId}/albums/{albumId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAlbumFromArtist(
            @PathVariable UUID artistId,
            @PathVariable UUID albumId
    ) {
        artistAlbumService.unlinkArtistFromAlbum(artistId, albumId);
    }

    @DeleteMapping("/{artistId}/genres/{genreId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeGenreFromArtist(
            @PathVariable UUID artistId,
            @PathVariable UUID genreId
    ) {
        artistGenreService.unlinkArtistFromGenre(artistId, genreId);
    }
}