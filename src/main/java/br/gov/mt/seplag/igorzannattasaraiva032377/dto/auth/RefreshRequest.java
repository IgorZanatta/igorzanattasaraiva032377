package br.gov.mt.seplag.igorzannattasaraiva032377.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Requisição para renovação de access token usando refresh token")
public record RefreshRequest(
        @Schema(
            description = "Refresh token JWT obtido no login ou renovação anterior. Pode ser enviado também via header Authorization.",
            example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwidHlwZSI6InJlZnJlc2giLCJpYXQiOjE3MDk1ODAwMDAsImV4cCI6MTcxMDc5MDAwMH0..."
        )
        String refreshToken
) {
}
