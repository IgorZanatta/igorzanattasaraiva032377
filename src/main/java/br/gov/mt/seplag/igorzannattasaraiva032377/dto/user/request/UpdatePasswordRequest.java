package br.gov.mt.seplag.igorzannattasaraiva032377.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequest(
        @NotBlank @Size(min = 6, max = 255) String newPassword
) {}
