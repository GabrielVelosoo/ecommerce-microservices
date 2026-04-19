package io.github.gabrielvelosoo.authservice.application.dto.register;

import io.github.gabrielvelosoo.authservice.application.validator.ValidateNotBlank;
import io.github.gabrielvelosoo.authservice.application.validator.ValidateOthers;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyRegisterCode(
        @Email(message = "Invalid email format", groups = ValidateOthers.class)
        @NotBlank(message = "Enter your email", groups = ValidateNotBlank.class)
        String email,

        @Size(min = 6, max = 6, message = "The code must have 6 digits", groups = ValidateOthers.class)
        @NotBlank(message = "Enter verification code", groups = ValidateNotBlank.class)
        String code
    ) {
}
