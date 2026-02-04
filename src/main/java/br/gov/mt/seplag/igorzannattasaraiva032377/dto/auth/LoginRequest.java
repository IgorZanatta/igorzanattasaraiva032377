package br.gov.mt.seplag.igorzannattasaraiva032377.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;


@Schema(description = "Credenciais para autenticação do usuário")
public record LoginRequest(
        @Schema(
            description = "Email do usuário (será normalizado para lowercase)",
            example = "test@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank String email,
        
        @Schema(
            description = "Senha do usuário",
            example = "Senha@123",
            format = "password",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank String password
) {
}
