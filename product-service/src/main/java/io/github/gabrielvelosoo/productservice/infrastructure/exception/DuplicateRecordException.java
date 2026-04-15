package io.github.gabrielvelosoo.productservice.infrastructure.exception;

public class DuplicateRecordException extends RuntimeException {

    public DuplicateRecordException(String message) {
        super(message);
    }
}
