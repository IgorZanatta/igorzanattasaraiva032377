package br.gov.mt.seplag.igorzannattasaraiva032377.controller.album;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
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

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.album.request.AlbumRequestDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.album.response.AlbumPageResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.album.response.AlbumResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.artist.response.ArtistResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.album.AlbumService;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.artist.ArtistService;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.artistAlbum.ArtistAlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;
    private final ArtistAlbumService artistAlbumService;
    private final ArtistService artistService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlbumResponseDTO create(@RequestBody @Valid AlbumRequestDTO dto) {
        return albumService.create(dto);
    }

    @GetMapping("/{id}")
    public AlbumResponseDTO findById(@PathVariable UUID id) {
        return albumService.findById(id);
    }

        @GetMapping
        public AlbumPageResponseDTO findAll(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer year,
            Pageable pageable
    ) {
        var page = (title != null)
            ? albumService.findByTitle(title, pageable)
            : (year != null)
                ? albumService.findByReleaseYear(year, pageable)
                : albumService.findAll(pageable);

        return new AlbumPageResponseDTO(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages()
        );
    }

    @PutMapping("/{id}")
    public AlbumResponseDTO update(
            @PathVariable UUID id,
            @RequestBody @Valid AlbumRequestDTO dto
    ) {
        return albumService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        albumService.delete(id);
    }

    @GetMapping("/{albumId}/artists")
    public List<ArtistResponseDTO> getArtistsByAlbum(@PathVariable UUID albumId) {
        var artistIds = artistAlbumService.getArtistIdsByAlbum(albumId);
        return artistIds.stream()
                .map(artistService::findById)
                .toList();
    }
}
