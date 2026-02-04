package br.gov.mt.seplag.igorzannattasaraiva032377.dto.album.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Página de resultados contendo álbuns e metadados de paginação")
public record AlbumPageResponseDTO(
        @Schema(
            description = "Lista de álbuns da página atual"
        )
        List<AlbumResponseDTO> content,
        
        @Schema(
            description = "Número da página atual (inicia em 0)",
            example = "0"
        )
        int page,
        
        @Schema(
            description = "Quantidade de itens por página",
            example = "10"
        )
        int size,
        
        @Schema(
            description = "Total de elementos encontrados em todas as páginas",
            example = "156"
        )
        long totalElements,
        
        @Schema(
            description = "Número total de páginas disponíveis",
            example = "16"
        )
        int totalPages
) {
}
