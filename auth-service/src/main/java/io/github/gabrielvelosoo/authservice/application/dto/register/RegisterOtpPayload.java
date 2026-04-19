package io.github.gabrielvelosoo.authservice.application.dto.register;

public record RegisterOtpPayload(
        String email,
        String firstName,
        String lastName,
        String password
    ) {
}
