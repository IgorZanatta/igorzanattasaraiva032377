package br.gov.mt.seplag.igorzannattasaraiva032377.dto.albumCover.response;


import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumCoverResponseDTO {

    private UUID id;

    private UUID albumId;

    private String objectKey;

    private String contentType;

    private Long fileSize;

    private Boolean isPrimary;

    /**
     * URL pré-assinada (expira em 30 minutos)
     */
    private String url;
}