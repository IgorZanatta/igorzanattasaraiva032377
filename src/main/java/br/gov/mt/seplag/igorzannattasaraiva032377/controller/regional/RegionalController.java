package br.gov.mt.seplag.igorzannattasaraiva032377.controller.regional;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.regional.response.RegionalResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.regional.RegionalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/regionais")
@RequiredArgsConstructor
@Tag(
    name = "Regionais",
    description = "Consulta de regionais administrativas via integração externa (API Argus)"
)
@SecurityRequirement(name = "bearerAuth")
public class RegionalController {

    private final RegionalService service;

    @GetMapping
    @Operation(
        summary = "Listar regionais ativas",
        description = """
            Retorna lista de todas as regionais administrativas ativas.
            
            **Fonte de dados:**
            - Integração com API externa (Argus)
            - Dados são sincronizados e armazenados localmente
            - Atualização automática em segundo plano
            
            **Filtro:**
            - Retorna apenas regionais com status `ativo=true`
            - Regionais inativas não são incluídas
            
            **Observações:**
            - Lista pode estar vazia se nenhuma regional estiver ativa
            - Dados são cacheados para performance
            - Sincronização manual disponível via endpoint `/sincronizar`
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de regionais ativas retornada com sucesso",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = RegionalResponseDTO.class))
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autenticado - token ausente ou inválido",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "503",
            description = "Serviço externo indisponível - erro na integração",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<List<RegionalResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarAtivas());
    }

    @PostMapping("/sincronizar")
    @Operation(
        summary = "Sincronizar regionais com API externa",
        description = """
            Força sincronização manual dos dados de regionais com a API externa.
            
            **Processo:**
            1. Consulta API externa (Argus)
            2. Atualiza registros existentes
            3. Insere novos registros
            4. Marca regionais removidas como inativas
            
            **Uso recomendado:**
            - Atualização manual em caso de inconsistências
            - Refresh forçado de dados
            - Processos de manutenção
            
            **Observações:**
            - Operação pode demorar alguns segundos
            - Sincronização automática já ocorre periodicamente
            - Retorna 204 (sem conteúdo) em caso de sucesso
            - Endpoint idempotente (pode ser chamado múltiplas vezes)
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Sincronização realizada com sucesso"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autenticado - token ausente ou inválido",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "503",
            description = "Serviço externo indisponível - erro na integração",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<Void> sincronizar() {
        service.sincronizar();
        return ResponseEntity.noContent().build();
    }
}