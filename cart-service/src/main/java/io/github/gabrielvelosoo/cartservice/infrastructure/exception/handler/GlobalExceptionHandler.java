package io.github.gabrielvelosoo.cartservice.infrastructure.exception.handler;

import io.github.gabrielvelosoo.cartservice.application.exception.BusinessException;
import io.github.gabrielvelosoo.cartservice.application.exception.RecordNotFoundException;
import io.github.gabrielvelosoo.cartservice.infrastructure.exception.KeycloakException;
import io.github.gabrielvelosoo.cartservice.infrastructure.exception.model.ErrorResponse;
import io.github.gabrielvelosoo.cartservice.infrastructure.exception.model.FieldError;
import io.github.gabrielvelosoo.cartservice.infrastructure.exception.model.ValidationErrorResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.error("Argument not valid error: ", e);
        int status = HttpStatus.UNPROCESSABLE_ENTITY.value();
        LocalDateTime timestamp = LocalDateTime.now();
        List<FieldError> errors = e.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> new FieldError(error.getField(), error.getDefaultMessage()))
                .toList();
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(status, timestamp, errors);
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        logger.error("Business exception: '{}'", e.getMessage());
        int status = HttpStatus.BAD_REQUEST.value();
        String message = e.getMessage();
        LocalDateTime timestamp = LocalDateTime.now();
        ErrorResponse errorResponse = new ErrorResponse(
                status,
                message,
                timestamp
        );
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRecordNotFoundException(RecordNotFoundException e) {
        logger.error("Record not found exception: '{}'", e.getMessage());
        int status = HttpStatus.NOT_FOUND.value();
        LocalDateTime timestamp = LocalDateTime.now();
        ErrorResponse errorResponse = new ErrorResponse(
                status,
                e.getMessage(),
                timestamp
        );
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler({ AuthorizationDeniedException.class, AccessDeniedException.class })
    public ResponseEntity<ErrorResponse> handleAccessDenied(AuthorizationDeniedException e) {
        logger.error("Authorization error: ", e);
        int status = HttpStatus.FORBIDDEN.value();
        LocalDateTime timestamp = LocalDateTime.now();
        ErrorResponse errorResponse = new ErrorResponse(
                status,
                "You do not have permission to access this resource",
                timestamp
        );
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(KeycloakException.class)
    public ResponseEntity<ErrorResponse> handleKeycloakException(KeycloakException e) {
        logger.error("Keycloak integration error: ", e);
        int status = HttpStatus.SERVICE_UNAVAILABLE.value();
        LocalDateTime timestamp = LocalDateTime.now();
        ErrorResponse errorResponse = new ErrorResponse(
                status,
                "Failed to communicate with authentication service. Please try again later.",
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
                "Unexpected error occurred. Please contact support.",
                timestamp
        );
        return ResponseEntity.status(status).body(errorResponse);
    }
}
