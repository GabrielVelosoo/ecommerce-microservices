package io.github.gabrielvelosoo.orderservice.infrastructure.exception.model;

public record FieldError(
        String fiel,
        String message
    ) {
}
