package io.github.gabrielvelosoo.authservice.application.dto.register;

import io.github.gabrielvelosoo.authservice.application.validator.ValidateNotBlank;
import io.github.gabrielvelosoo.authservice.application.validator.ValidateOthers;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendRegisterCode(
        @Email(message = "Invalid email format", groups = ValidateOthers.class)
        @NotBlank(message = "Enter your e-mail", groups = ValidateNotBlank.class)
        String email,

        @NotBlank(message = "Enter your first name", groups = ValidateNotBlank.class)
        String firstName,

        @NotBlank(message = "Enter your last name", groups = ValidateNotBlank.class)
        String lastName,

        @NotBlank(message = "Enter your password", groups = ValidateNotBlank.class)
        String password
    ) {
}
