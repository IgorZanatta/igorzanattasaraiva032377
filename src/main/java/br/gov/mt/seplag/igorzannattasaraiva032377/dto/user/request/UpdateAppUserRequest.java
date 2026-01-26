package br.gov.mt.seplag.igorzannattasaraiva032377.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateAppUserRequest(
        @Size(max = 255) String name,
        @Email @Size(max = 255) String email,
        Boolean active
) {}

