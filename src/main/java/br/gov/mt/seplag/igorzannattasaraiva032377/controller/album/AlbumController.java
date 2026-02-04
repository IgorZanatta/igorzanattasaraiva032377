package br.gov.mt.seplag.igorzannattasaraiva032377.controller.album;

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
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistType;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.album.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/albums")
@RequiredArgsConstructor
@Tag(
    name = "Albums",
    description = "Gerenciamento de álbuns musicais com suporte a paginação e filtros"
)
@SecurityRequirement(name = "bearerAuth")
public class AlbumController {

    private final AlbumService albumService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Criar novo álbum",
        description = """
            Cria um novo álbum com título, ano de lançamento e artistas opcionais.
            
            **Validações:**
            - Título é obrigatório (máximo 255 caracteres)
            - Ano de lançamento é opcional (inteiro válido)
            - Título não pode conter apenas espaços
            - IDs de artistas devem existir no banco (se fornecidos)
            
            **Vinculação de artistas (opcional):**
            - Forneça `artistIds` para vincular artistas na criação
            - Se omitido ou vazio, álbum é criado sem artistas
            - Artistas inválidos geram warning mas não impedem a criação
            - Artistas podem ser vinculados posteriormente via endpoints específicos
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Álbum criado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AlbumResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos - validação de campos falhou",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autenticado - token ausente ou inválido",
            content = @Content(mediaType = "application/json")
        )
    })
    public AlbumResponseDTO create(@RequestBody @Valid AlbumRequestDTO dto) {
        return albumService.create(dto);
    }

    @GetMapping
    @Operation(
        summary = "Listar álbuns com paginação",
        description = """
            Lista álbuns com filtros opcionais e paginação.
            
            **Filtros disponíveis (aplicados individualmente):**
            - `title`: Busca parcial case-insensitive no título
            - `year`: Filtra por ano exato de lançamento
            - `artistType`: Filtra álbuns por tipo de artista associado (SOLO ou BAND)
            
            **Paginação:**
            - `page`: Número da página (inicia em 0)
            - `size`: Quantidade de itens por página
            - `sort`: Ordenação (ex: `title,asc` ou `releaseYear,desc`)
            
            **Observações:**
            - Apenas um filtro é aplicado por vez (prioridade: artistType > title > year)
            - Retorna objeto paginado com metadados
            - Página vazia se nenhum resultado for encontrado
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Página de álbuns retornada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AlbumPageResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Parâmetros de paginação ou filtro inválidos",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autenticado - token ausente ou inválido",
            content = @Content(mediaType = "application/json")
        )
    })
    public AlbumPageResponseDTO findAll(
            @Parameter(description = "Filtro parcial por título do álbum (busca case-insensitive)")
            @RequestParam(required = false) String title,
            @Parameter(
                description = "Filtro por ano exato de lançamento",
                example = "2023"
            )
            @RequestParam(required = false) Integer year,
            @Parameter(
                description = "Filtro por tipo de artista associado ao álbum",
                schema = @Schema(allowableValues = {"SOLO", "BAND"})
            )
            @RequestParam(required = false) ArtistType artistType,
            @Parameter(
                description = "Configuração de paginação e ordenação (page, size, sort)",
                example = "page=0&size=10&sort=title,asc"
            )
            Pageable pageable
    ) {
        var page = (artistType != null)
                ? albumService.findByArtistType(artistType, pageable)
                : (title != null)
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
    @Operation(
        summary = "Atualizar álbum existente",
        description = """
            Atualiza título e ano de lançamento de um álbum existente.
            
            **Comportamento:**
            - Substitui completamente os dados anteriores
            - Não afeta as associações com artistas ou capas
            
            **Validações:**
            - Álbum deve existir (404 se não encontrado)
            - Mesmas validações do endpoint de criação
            
            **Observações:**
            - Não é possível atualização parcial (PATCH)
            - Todos os campos obrigatórios devem ser enviados
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Álbum atualizado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AlbumResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos - validação de campos falhou",
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
    public AlbumResponseDTO update(
            @Parameter(
                description = "UUID do álbum a ser atualizado",
                example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @PathVariable UUID id,
            @RequestBody @Valid AlbumRequestDTO dto
    ) {
        return albumService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Excluir álbum",
        description = """
            Remove permanentemente um álbum e todas as suas associações.
            
            **Comportamento:**
            - Deleta o álbum do banco de dados
            - Remove associações com artistas
            - Remove capas de álbum associadas (arquivos e registros)
            - Operação irreversível
            
            **Observações:**
            - Retorna 204 mesmo se o álbum não existir
            - Cascata: todas as dependências são removidas
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Álbum excluído com sucesso (ou já não existia)"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autenticado - token ausente ou inválido",
            content = @Content(mediaType = "application/json")
        )
    })
    public void delete(
            @Parameter(
                description = "UUID do álbum a ser excluído",
                example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @PathVariable UUID id
    ) {
        albumService.delete(id);
    }

}
