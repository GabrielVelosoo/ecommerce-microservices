package io.github.gabrielvelosoo.authservice.domain.exception.model;

import java.time.LocalDateTime;

public record ErrorResponse(
        Integer status,
        String message,
        LocalDateTime timestamp
    ) {
}
