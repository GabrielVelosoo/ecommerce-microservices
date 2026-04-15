package io.github.gabrielvelosoo.authservice.domain.exception.model;

public record FieldError(
        String field,
        String message
    ) {
}
