package io.github.gabrielvelosoo.orderservice.infrastructure.exception.model;

import java.time.LocalDateTime;

public record ErrorResponse(
        Integer status,
        String message,
        LocalDateTime timestamp
    ) {
}
