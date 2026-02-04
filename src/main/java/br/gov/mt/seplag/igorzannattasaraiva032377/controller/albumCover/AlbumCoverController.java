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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/albums/{albumId}/covers")
@RequiredArgsConstructor
@Tag(
    name = "Album Covers",
    description = "Upload e gerenciamento de capas de álbuns (imagens armazenadas em MinIO)"
)
@SecurityRequirement(name = "bearerAuth")
public class AlbumCoverController {

    private final AlbumCoverService albumCoverService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Upload de capas de álbum",
        description = """
            Faz upload de uma ou mais imagens de capa para um álbum específico.
            
            **Funcionalidades:**
            - Aceita múltiplos arquivos simultaneamente
            - Armazena imagens no MinIO (object storage)
            - Primeira capa enviada é marcada como primária automaticamente
            - Gera URLs pré-assinadas para acesso às imagens
            
            **Formatos aceitos:**
            - JPEG (.jpg, .jpeg)
            - PNG (.png)
            - GIF (.gif)
            - WebP (.webp)
            
            **Validações:**
            - Álbum deve existir (404 se não encontrado)
            - Arquivos devem ser imagens válidas
            - Tamanho máximo por arquivo configurável
            
            **Observações:**
            - URLs retornadas expiram em 30 minutos
            - Metadados (tipo, tamanho) são armazenados no banco
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Capas enviadas e armazenadas com sucesso",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = AlbumCoverResponseDTO.class))
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Arquivo inválido ou formato não suportado",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autenticado - token ausente ou inválido",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Álbum não encontrado com o ID fornecido",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<List<AlbumCoverResponseDTO>> uploadCovers(
            @Parameter(
                description = "UUID do álbum ao qual as capas serão associadas",
                example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @PathVariable UUID albumId,
            @Parameter(
                description = "Arquivos de imagem a serem enviados (aceita múltiplos arquivos)",
                content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestParam("files") List<MultipartFile> files
    ) {
        List<AlbumCoverResponseDTO> response =
                albumCoverService.uploadCovers(albumId, files);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping
    @Operation(
        summary = "Listar todas as capas de um álbum",
        description = """
            Retorna lista completa de capas associadas a um álbum específico.
            
            **Informações retornadas:**
            - ID da capa
            - ID do álbum
            - Chave do objeto no MinIO
            - Tipo de conteúdo (MIME type)
            - Tamanho do arquivo em bytes
            - Indicador de capa primária
            - URL pré-assinada para download/visualização
            
            **Observações:**
            - URLs pré-assinadas expiram em 30 minutos
            - Lista vazia se álbum não tiver capas
            - Ordenação por data de criação (mais antiga primeiro)
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de capas retornada com sucesso (pode estar vazia)",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = AlbumCoverResponseDTO.class))
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autenticado - token ausente ou inválido",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Álbum não encontrado com o ID fornecido",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<List<AlbumCoverResponseDTO>> listCovers(
            @Parameter(
                description = "UUID do álbum cujas capas serão listadas",
                example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @PathVariable UUID albumId
    ) {
        return ResponseEntity.ok(
                albumCoverService.listCoversWithPresignedUrls(albumId)
        );
    }


    @GetMapping("/primary")
    @Operation(
        summary = "Obter capa primária do álbum",
        description = """
            Retorna a capa marcada como primária de um álbum específico.
            
            **Comportamento:**
            - Retorna apenas uma capa (a marcada como primária)
            - Se álbum não tiver capa primária, lança exceção 404
            - URL pré-assinada incluída na resposta
            
            **Uso típico:**
            - Exibição de thumbnail do álbum
            - Capa padrão para listagens
            - Preview rápido
            
            **Observações:**
            - URL expira em 30 minutos
            - Apenas uma capa pode ser primária por álbum
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Capa primária retornada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AlbumCoverResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autenticado - token ausente ou inválido",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Álbum não encontrado ou não possui capa primária",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<AlbumCoverResponseDTO> getPrimaryCover(
            @Parameter(
                description = "UUID do álbum cuja capa primária será retornada",
                example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @PathVariable UUID albumId
    ) {
        return ResponseEntity.ok(
                albumCoverService.getPrimaryCover(albumId)
        );
    }
}
