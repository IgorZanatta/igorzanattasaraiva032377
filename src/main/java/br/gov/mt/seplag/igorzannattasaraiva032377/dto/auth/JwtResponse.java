package br.gov.mt.seplag.igorzannattasaraiva032377.dto.auth;

import java.util.UUID;

public record JwtResponse(
        String accessToken,
        String refreshToken,
        UUID userId,
        String name,
        String email
) {
}
