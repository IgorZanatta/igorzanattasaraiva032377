package br.gov.mt.seplag.igorzannattasaraiva032377.exception;

import java.time.LocalDateTime;

public record ApiErrorResponse(
        int status,
        String error,
        String message,
        String path,
        LocalDateTime timestamp
) {}