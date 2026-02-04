package br.gov.mt.seplag.igorzannattasaraiva032377.dto.auth;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta de autenticação contendo tokens JWT e dados do usuário")
public record JwtResponse(
        @Schema(
            description = "Access token JWT (curta duração) - usar nas requisições autenticadas",
            example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwidHlwZSI6ImFjY2VzcyIsImlhdCI6MTcwOTU4MDAwMCwiZXhwIjoxNzA5NTgzNjAwfQ..."
        )
        String accessToken,
        
        @Schema(
            description = "Refresh token JWT (longa duração) - usar apenas para renovação de tokens",
            example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwidHlwZSI6InJlZnJlc2giLCJpYXQiOjE3MDk1ODAwMDAsImV4cCI6MTcxMDc5MDAwMH0..."
        )
        String refreshToken,
        
        @Schema(
            description = "ID único do usuário autenticado",
            example = "123e4567-e89b-12d3-a456-426614174000"
        )
        UUID userId,
        
        @Schema(
            description = "Nome completo do usuário",
            example = "Igor Zanatta"
        )
        String name,
        
        @Schema(
            description = "Email do usuário (usado como username)",
            example = "igor.zanatta@seplag.mt.gov.br"
        )
        String email
) {
}
