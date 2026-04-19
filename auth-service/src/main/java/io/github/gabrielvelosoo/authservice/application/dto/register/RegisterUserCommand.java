package io.github.gabrielvelosoo.authservice.application.dto.register;

public record RegisterUserCommand(
        String email,
        String firstName,
        String lastName,
        String password
    ) {
}
