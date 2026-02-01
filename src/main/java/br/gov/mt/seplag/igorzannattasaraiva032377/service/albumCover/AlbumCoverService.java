package br.gov.mt.seplag.igorzannattasaraiva032377.service.albumCover;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.albumCover.response.AlbumCoverResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface AlbumCoverService {

    List<AlbumCoverResponseDTO> uploadCovers(
            UUID albumId,
            List<MultipartFile> files
    );

    List<AlbumCoverResponseDTO> listCoversWithPresignedUrls(UUID albumId);

    AlbumCoverResponseDTO getPrimaryCover(UUID albumId);
}