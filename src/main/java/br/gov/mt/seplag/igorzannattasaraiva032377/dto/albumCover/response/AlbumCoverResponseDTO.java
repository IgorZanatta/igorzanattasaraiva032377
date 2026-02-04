package br.gov.mt.seplag.igorzannattasaraiva032377.dto.albumCover.response;


import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Dados de uma capa de álbum armazenada no MinIO com URL pré-assinada")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumCoverResponseDTO {

    @Schema(
        description = "Identificador único da capa",
        example = "123e4567-e89b-12d3-a456-426614174000"
    )
    private UUID id;

    @Schema(
        description = "ID do álbum ao qual a capa pertence",
        example = "987fcdeb-51a2-43d7-b890-123456789abc"
    )
    private UUID albumId;

    @Schema(
        description = "Chave (path) do objeto armazenado no MinIO",
        example = "album-covers/987fcdeb-51a2-43d7-b890-123456789abc/cover-1.jpg"
    )
    private String objectKey;

    @Schema(
        description = "Tipo MIME do arquivo de imagem",
        example = "image/jpeg",
        allowableValues = {"image/jpeg", "image/png", "image/gif", "image/webp"}
    )
    private String contentType;

    @Schema(
        description = "Tamanho do arquivo em bytes",
        example = "524288"
    )
    private Long fileSize;

    @Schema(
        description = "Indica se esta é a capa primária do álbum (true para a primeira capa enviada)",
        example = "true"
    )
    private Boolean isPrimary;

    @Schema(
        description = "URL pré-assinada para acesso temporário à imagem (expira em 30 minutos)",
        example = "https://minio.example.com/bucket/album-covers/123/cover.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=..."
    )
    private String url;
}