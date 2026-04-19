package io.github.gabrielvelosoo.authservice.application.dto.error;

public record FieldError(
        String field,
        String message
    ) {
}
