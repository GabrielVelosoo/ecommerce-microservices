package io.github.gabrielvelosoo.authservice.application.dto.email;

import io.github.gabrielvelosoo.authservice.application.validator.ValidateNotBlank;
import io.github.gabrielvelosoo.authservice.application.validator.ValidateOthers;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CheckEmailRequest(
        @Email(message = "Invalid email format", groups = ValidateOthers.class)
        @NotBlank(message = "Required field", groups = ValidateNotBlank.class)
        String email
    ) {
}
