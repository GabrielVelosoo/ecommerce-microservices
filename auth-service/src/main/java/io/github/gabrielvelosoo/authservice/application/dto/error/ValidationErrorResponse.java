package io.github.gabrielvelosoo.authservice.application.dto.error;

import java.time.LocalDateTime;
import java.util.List;

public record ValidationErrorResponse(
        Integer status,
        LocalDateTime timestamp,
        List<FieldError> errors
    ) {
}
