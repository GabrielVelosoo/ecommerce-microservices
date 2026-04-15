package io.github.gabrielvelosoo.productservice.infrastructure.exception;

public class RecordNotFoundException extends RuntimeException {

    public RecordNotFoundException(String message) {
        super(message);
    }
}
