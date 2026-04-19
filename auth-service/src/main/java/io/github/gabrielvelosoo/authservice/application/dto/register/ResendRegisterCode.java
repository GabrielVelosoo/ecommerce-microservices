package io.github.gabrielvelosoo.authservice.application.dto.register;

import io.github.gabrielvelosoo.authservice.application.validator.ValidateNotBlank;
import io.github.gabrielvelosoo.authservice.application.validator.ValidateOthers;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResendRegisterCode(
        @Email(message = "Invalid email format", groups = ValidateOthers.class)
        @NotBlank(message = "Enter your email", groups = ValidateNotBlank.class)
        String email
    ) {
}
