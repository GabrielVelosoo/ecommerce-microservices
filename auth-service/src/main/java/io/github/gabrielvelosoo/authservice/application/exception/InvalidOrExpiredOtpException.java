package io.github.gabrielvelosoo.authservice.application.exception;

public class InvalidOrExpiredOtpException extends RuntimeException {

    public InvalidOrExpiredOtpException(String message) {
        super(message);
    }
}
