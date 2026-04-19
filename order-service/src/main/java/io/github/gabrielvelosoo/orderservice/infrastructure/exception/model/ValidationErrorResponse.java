package io.github.gabrielvelosoo.orderservice.infrastructure.exception.model;

import java.time.LocalDateTime;
import java.util.List;

public record ValidationErrorResponse(
        Integer status,
        LocalDateTime timestamp,
        List<FieldError> errors
    ) {
}
