package io.github.gabrielvelosoo.cartservice.infrastructure.exception.model;

public record FieldError(
        String fiel,
        String message
    ) {
}
