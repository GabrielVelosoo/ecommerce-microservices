package io.github.gabrielvelosoo.authservice.application.exception;

public class OtpAttemptsExceededException extends RuntimeException {

    public OtpAttemptsExceededException(String message) {
        super(message);
    }
}
