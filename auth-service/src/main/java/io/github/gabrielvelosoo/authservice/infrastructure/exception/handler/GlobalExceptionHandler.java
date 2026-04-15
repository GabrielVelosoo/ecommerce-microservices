package io.github.gabrielvelosoo.authservice.infrastructure.exception.handler;

import io.github.gabrielvelosoo.authservice.domain.exception.model.ErrorResponse;
import io.github.gabrielvelosoo.authservice.domain.exception.model.FieldError;
import io.github.gabrielvelosoo.authservice.domain.exception.model.ValidationErrorResponse;
import io.github.gabrielvelosoo.authservice.infrastructure.exception.KeycloakException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.error(e.getMessage());
        int status = HttpStatus.UNPROCESSABLE_ENTITY.value();
        LocalDateTime timestamp = LocalDateTime.now();
        List<FieldError> errors = e.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> new FieldError(error.getField(), error.getDefaultMessage()))
                .toList();
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(status, timestamp, errors);
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(KeycloakException.class)
    public ResponseEntity<ErrorResponse> handleKeycloakException(KeycloakException e) {
        logger.error("Keycloak operation failed", e);
        int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        LocalDateTime timestamp = LocalDateTime.now();
        ErrorResponse errorResponse = new ErrorResponse(
                status,
                "An error occurred while processing Keycloak operation",
                timestamp
        );
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleUnhandledErrorsException(RuntimeException e) {
        logger.error("Unhandled exception: ", e);
        int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        LocalDateTime timestamp = LocalDateTime.now();
        ErrorResponse errorResponse = new ErrorResponse(
                status,
                "Unhandled error, contact development",
                timestamp
        );
        return ResponseEntity.status(status).body(errorResponse);
    }
}
