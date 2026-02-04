package br.gov.mt.seplag.igorzannattasaraiva032377.controller.artist;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.artist.request.ArtistRequestDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.artist.response.ArtistResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistType;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.artist.ArtistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/artists")
@RequiredArgsConstructor
@Tag(
    name = "Artists",
    description = "Gerenciamento de artistas musicais (solo ou bandas) e seus gêneros"
)
@SecurityRequirement(name = "bearerAuth")
public class ArtistController {

    private final ArtistService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Criar novo artista",
        description = """
            Cria um novo artista com nome, tipo e gêneros musicais associados.
            
            **Tipo de artista:**
            - `SOLO`: Artista individual
            - `BAND`: Banda/Grupo musical
            
            **Gêneros:**
            - Lista de nomes de gêneros (criados automaticamente se não existirem)
            - Campo opcional
            - Gêneros duplicados são tratados automaticamente
            
            **Validações:**
            - Nome é obrigatório (máximo 255 caracteres)
            - Tipo é obrigatório
            - Nome não pode conter apenas espaços
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Artista criado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ArtistResponseDTO.class)
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
            responseCode = "403",
            description = "Acesso negado - token válido mas sem permissão",
            content = @Content(mediaType = "application/json")
        )
    })
    public ArtistResponseDTO create(@RequestBody @Valid ArtistRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping
    @Operation(
        summary = "Listar artistas",
        description = """
            Lista todos os artistas com filtros opcionais e ordenação.
            
            **Filtros disponíveis:**
            - `name`: Busca parcial case-insensitive no nome (ex: "john" encontra "John Doe")
            - `type`: Filtra por tipo de artista (SOLO ou BAND)
            
            **Ordenação:**
            - `sort=asc`: Ordem alfabética crescente (padrão)
            - `sort=desc`: Ordem alfabética decrescente
            
            **Observações:**
            - Retorna lista vazia se nenhum artista for encontrado
            - Todos os filtros são opcionais
            - Filtros podem ser combinados
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de artistas retornada com sucesso (pode estar vazia)",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = ArtistResponseDTO.class))
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Parâmetros de filtro inválidos",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autenticado - token ausente ou inválido",
            content = @Content(mediaType = "application/json")
        )
    })
    public List<ArtistResponseDTO> findAll(
            @Parameter(description = "Filtro parcial por nome do artista (busca case-insensitive)")
            @RequestParam(required = false) String name,
            @Parameter(
                description = "Filtro por tipo de artista",
                schema = @Schema(allowableValues = {"SOLO", "BAND"})
            )
            @RequestParam(required = false) ArtistType type,
            @Parameter(
                description = "Direção da ordenação alfabética por nome",
                schema = @Schema(allowableValues = {"asc", "desc"}, defaultValue = "asc")
            )
            @RequestParam(required = false, defaultValue = "asc") String sort
    ) {
        return service.findAll(name, type, sort);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Atualizar artista existente",
        description = """
            Atualiza nome, tipo e gêneros de um artista existente.
            
            **Comportamento:**
            - Substitui completamente os dados anteriores
            - Gêneros são recriados (remove associações antigas e cria novas)
            - Gêneros novos são criados automaticamente se não existirem
            
            **Validações:**
            - Artista deve existir (404 se não encontrado)
            - Mesmas validações do endpoint de criação
            
            **Observações:**
            - Não é possível atualização parcial (PATCH)
            - Todos os campos obrigatórios devem ser enviados
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Artista atualizado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ArtistResponseDTO.class)
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
            description = "Artista não encontrado com o ID fornecido",
            content = @Content(mediaType = "application/json")
        )
    })
    public ArtistResponseDTO update(
            @Parameter(
                description = "UUID do artista a ser atualizado",
                example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @PathVariable UUID id,
            @RequestBody @Valid ArtistRequestDTO dto
    ) {
        return service.update(id, dto);
    }
}