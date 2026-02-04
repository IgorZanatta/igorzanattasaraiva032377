package br.gov.mt.seplag.igorzannattasaraiva032377.controller.genre;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.genre.request.GenreRequestDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.genre.response.GenreResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.genre.GenreService;
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
@RequestMapping("/api/v1/genres")
@RequiredArgsConstructor
@Tag(
    name = "Genres",
    description = "Gerenciamento de gêneros musicais com controle de status ativo/inativo"
)
@SecurityRequirement(name = "bearerAuth")
public class GenreController {

    private final GenreService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Criar novo gênero musical",
        description = """
            Cria um novo gênero musical com nome e status de ativação.
            
            **Validações:**
            - Nome é obrigatório (máximo 120 caracteres)
            - Nome deve ser único (case-insensitive)
            - Nome não pode conter apenas espaços
            
            **Status:**
            - `active=true`: Gênero ativo e disponível para uso (padrão)
            - `active=false`: Gênero inativo (não aparece em listagens padrão)
            
            **Observações:**
            - Gêneros são normalizados (trim e capitalização)
            - Duplicatas são detectadas automaticamente
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Gênero criado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GenreResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos - validação de campos falhou ou gênero duplicado",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autenticado - token ausente ou inválido",
            content = @Content(mediaType = "application/json")
        )
    })
    public GenreResponseDTO create(@Valid @RequestBody GenreRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping
    @Operation(
        summary = "Listar gêneros musicais",
        description = """
            Lista gêneros com filtro opcional por nome e ordenação.
            
            **Filtros disponíveis:**
            - `name`: Busca parcial case-insensitive no nome do gênero
            - `sort`: Ordenação alfabética (asc ou desc)
            
            **Comportamento:**
            - Sem filtros: retorna todos os gêneros (ativos e inativos)
            - Com filtro de nome: retorna gêneros que contenham o termo
            
            **Ordenação:**
            - `sort=asc`: Ordem alfabética crescente (padrão)
            - `sort=desc`: Ordem alfabética decrescente
            
            **Observações:**
            - Retorna lista vazia se nenhum gênero for encontrado
            - Busca é case-insensitive
            - Inclui gêneros ativos e inativos
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de gêneros retornada com sucesso (pode estar vazia)",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = GenreResponseDTO.class))
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
    public List<GenreResponseDTO> findAll(
            @Parameter(description = "Filtro parcial por nome do gênero (busca case-insensitive)")
            @RequestParam(required = false) String name,
            @Parameter(
                description = "Direção da ordenação alfabética por nome",
                schema = @Schema(allowableValues = {"asc", "desc"}, defaultValue = "asc")
            )
            @RequestParam(required = false, defaultValue = "asc") String sort
    ) {
        if (name != null) {
            return service.findByName(name, sort);
        }
        return service.findAll();
    }
}
