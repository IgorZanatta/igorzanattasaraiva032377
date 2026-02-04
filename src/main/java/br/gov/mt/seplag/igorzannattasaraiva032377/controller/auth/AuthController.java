package br.gov.mt.seplag.igorzannattasaraiva032377.controller.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.auth.JwtResponse;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.auth.LoginRequest;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.auth.RefreshRequest;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(
    name = "Auth",
    description = "Autenticação e gerenciamento de tokens JWT (access e refresh)"
)
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
        summary = "Autenticar usuário",
        description = """
            Realiza autenticação com email e senha, retornando access token e refresh token.
            
            **Fluxo:**
            1. Valida credenciais do usuário
            2. Gera access token (curta duração) e refresh token (longa duração)
            3. Registra data/hora do login
            4. Retorna tokens e informações básicas do usuário
            
            **Observações:**
            - Email é normalizado (lowercase e trim)
            - Access token deve ser usado nas requisições autenticadas
            - Refresh token deve ser usado apenas para renovação
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Autenticação realizada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = JwtResponse.class),
                examples = @ExampleObject(
                    value = """
                        {
                          "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
                          "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
                          "userId": "123e4567-e89b-12d3-a456-426614174000",
                          "name": "Igor Zanatta",
                          "email": "igor.zanatta@seplag.mt.gov.br"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Requisição inválida - campos obrigatórios ausentes ou inválidos",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Credenciais inválidas - email ou senha incorretos",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest request) {
        JwtResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(
        summary = "Renovar access token",
        description = """
            Renova o access token usando um refresh token válido. Endpoint público.
            
            **Aceita refresh token de duas formas:**
            1. No body (campo `refreshToken`)
            2. No header `Authorization: Bearer {refreshToken}`
            
            **Fluxo:**
            1. Valida o refresh token
            2. Verifica se não está na blacklist
            3. Confirma que é do tipo 'refresh'
            4. Gera novos access e refresh tokens
            5. Blacklista o refresh token antigo (token rotation)
            
            **Observações:**
            - Implementa token rotation para maior segurança
            - O refresh token antigo é invalidado após uso
            - Retorna nova dupla de tokens
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Tokens renovados com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = JwtResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Refresh token não fornecido ou requisição inválida",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Refresh token inválido, expirado, blacklistado ou tipo incorreto",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<JwtResponse> refresh(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody(required = false) RefreshRequest request) {

        // Extrai refresh token do body ou header
        String refreshToken = extractRefreshToken(authHeader, request);
        
        JwtResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(
        summary = "Encerrar sessão (logout)",
        description = """
            Invalida tokens ativos adicionando-os à blacklist. Endpoint protegido (requer autenticação).
            
            **Aceita tokens para blacklist:**
            - Access token: via header `Authorization: Bearer {accessToken}`
            - Refresh token: via body (campo `refreshToken`)
            
            **Fluxo:**
            1. Extrai tokens da requisição (header e/ou body)
            2. Valida cada token
            3. Adiciona tokens válidos à blacklist até sua expiração natural
            4. Retorna 204 No Content
            
            **Observações:**
            - Tokens blacklistados não podem mais ser usados
            - Blacklist é temporária (até expiração do token)
            - É recomendado enviar ambos os tokens para segurança total
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Logout realizado com sucesso - tokens invalidados"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token de autenticação inválido ou ausente",
            content = @Content(mediaType = "application/json")
        )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody(required = false) RefreshRequest request) {

        // Extrai tokens para blacklist
        String accessToken = extractAccessToken(authHeader);
        String refreshToken = extractRefreshTokenFromBody(request);

        authService.logout(accessToken, refreshToken);
        
        return ResponseEntity.noContent().build();
    }

    /**
     * Extrai refresh token do header Authorization ou do body da requisição.
     */
    private String extractRefreshToken(String authHeader, RefreshRequest request) {
        String refreshToken = null;

        // Tenta pegar do body primeiro
        if (request != null && StringUtils.hasText(request.refreshToken())) {
            refreshToken = request.refreshToken().trim();
        }

        // Se não veio no body, tenta pegar do header Authorization
        if (!StringUtils.hasText(refreshToken) && authHeader != null && authHeader.startsWith("Bearer ")) {
            refreshToken = authHeader.substring("Bearer ".length()).trim();
        }

        return refreshToken;
    }

    /**
     * Extrai access token do header Authorization.
     */
    private String extractAccessToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring("Bearer ".length()).trim();
        }
        return null;
    }

    /**
     * Extrai refresh token do body da requisição.
     */
    private String extractRefreshTokenFromBody(RefreshRequest request) {
        if (request != null && StringUtils.hasText(request.refreshToken())) {
            return request.refreshToken().trim();
        }
        return null;
    }
}
