package br.gov.mt.seplag.igorzannattasaraiva032377.dto.user.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppUserResponse(
        UUID id,
        String name,
        String email,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime lastLogin
) {}