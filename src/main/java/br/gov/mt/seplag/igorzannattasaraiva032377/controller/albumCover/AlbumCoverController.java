package br.gov.mt.seplag.igorzannattasaraiva032377.controller.albumCover;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.albumCover.response.AlbumCoverResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.albumCover.AlbumCoverService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/albums/{albumId}/covers")
@RequiredArgsConstructor
public class AlbumCoverController {

    private final AlbumCoverService albumCoverService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<AlbumCoverResponseDTO>> uploadCovers(
            @PathVariable UUID albumId,
            @RequestParam("files") List<MultipartFile> files
    ) {
        List<AlbumCoverResponseDTO> response =
                albumCoverService.uploadCovers(albumId, files);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping
    public ResponseEntity<List<AlbumCoverResponseDTO>> listCovers(
            @PathVariable UUID albumId
    ) {
        return ResponseEntity.ok(
                albumCoverService.listCoversWithPresignedUrls(albumId)
        );
    }


    @GetMapping("/primary")
    public ResponseEntity<AlbumCoverResponseDTO> getPrimaryCover(
            @PathVariable UUID albumId
    ) {
        return ResponseEntity.ok(
                albumCoverService.getPrimaryCover(albumId)
        );
    }
}
