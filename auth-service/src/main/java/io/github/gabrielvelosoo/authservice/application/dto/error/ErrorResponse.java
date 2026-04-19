package io.github.gabrielvelosoo.authservice.application.dto.error;

import java.time.LocalDateTime;

public record ErrorResponse(
        Integer status,
        String message,
        LocalDateTime timestamp
    ) {
}
