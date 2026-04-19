package io.github.gabrielvelosoo.cartservice.infrastructure.exception.model;

import java.time.LocalDateTime;

public record ErrorResponse(
        Integer status,
        String message,
        LocalDateTime timestamp
    ) {
}
