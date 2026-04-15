package io.github.gabrielvelosoo.authservice.infrastructure.exception;

public class KeycloakException extends RuntimeException {
    public KeycloakException(String message) {
        super(message);
    }
}
