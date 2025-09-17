package com.cymark.estatemanagementsystem.exception;

import com.cymark.estatemanagementsystem.model.response.BaseResponse;
import com.cymark.estatemanagementsystem.model.response.ErrorResponse;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    // Generic exception handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<?>> handleException(Exception ex) {
        log.error("Unhandled exception occurred: {}", ex.getMessage(), ex);

        return new ResponseEntity<>(
                BaseResponse.error(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "An unexpected error occurred. Please try again later."
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    // Custom business exceptions
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<BaseResponse<?>> handleApiException(ApiException ex) {
        HttpStatus status = status(ex.getCode());
        return new ResponseEntity<>(
                BaseResponse.error(status, ex.getMessage()),
                status
        );
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<BaseResponse<?>> handleUserException(UserException ex) {
        HttpStatus status = ex.getStatus() != null ? ex.getStatus().getHttpStatus() :
                (ex.getCode() >= 200 ? status(ex.getCode()) : HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(
                BaseResponse.error(status, ex.getMessage()),
                status
        );
    }

    @ExceptionHandler(CymarkException.class)
    public ResponseEntity<BaseResponse<?>> handleCymarkException(CymarkException ex) {
        return new ResponseEntity<>(
                BaseResponse.error(HttpStatus.BAD_REQUEST, ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    // Authentication Exceptions (401 UNAUTHORIZED)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<BaseResponse<?>> handleBadCredentialsException(BadCredentialsException ex) {
        return new ResponseEntity<>(
                BaseResponse.error(HttpStatus.UNAUTHORIZED, "Invalid username or password"),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler({AuthenticationException.class, InsufficientAuthenticationException.class})
    public ResponseEntity<BaseResponse<?>> handleAuthenticationException(AuthenticationException ex) {
        return new ResponseEntity<>(
                BaseResponse.error(HttpStatus.UNAUTHORIZED, "Authentication required: " + ex.getMessage()),
                HttpStatus.UNAUTHORIZED
        );
    }

    // Authorization Exceptions (403 FORBIDDEN)
    @ExceptionHandler({AuthorizationDeniedException.class, AccessDeniedException.class})
    public ResponseEntity<BaseResponse<?>> handleAuthorizationException(RuntimeException ex) {
        log.warn("Authorization denied: {}", ex.getMessage());
        return new ResponseEntity<>(
                BaseResponse.error(HttpStatus.FORBIDDEN, "Access denied. You don't have permission to perform this action."),
                HttpStatus.FORBIDDEN
        );
    }

    // JWT Specific Exceptions
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<BaseResponse<?>> handleExpiredJwtException(ExpiredJwtException ex) {
        log.warn("JWT token expired: {}", ex.getMessage());
        return new ResponseEntity<>(
                BaseResponse.error(HttpStatus.UNAUTHORIZED, "Your session has expired. Please login again."),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler({SignatureException.class, MalformedJwtException.class})
    public ResponseEntity<BaseResponse<?>> handleJwtException(Exception ex) {
        log.warn("JWT validation failed: {}", ex.getMessage());
        return new ResponseEntity<>(
                BaseResponse.error(HttpStatus.UNAUTHORIZED, "Invalid token. Please login again."),
                HttpStatus.UNAUTHORIZED
        );
    }

    // Validation Exceptions
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        // Convert field errors to a readable string
        String errorMessage = "Validation failed. Please check your input.";
        if (!fieldErrors.isEmpty()) {
            errorMessage += " Errors: " + fieldErrors.toString();
        }

        return new ResponseEntity<>(
                BaseResponse.error(HttpStatus.BAD_REQUEST, errorMessage),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseResponse<?>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Invalid parameter '%s' with value '%s'. Expected type: %s",
                ex.getName(),
                ex.getValue(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"
        );

        return new ResponseEntity<>(
                BaseResponse.error(HttpStatus.BAD_REQUEST, message),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(
                BaseResponse.error(HttpStatus.BAD_REQUEST, ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    // HTTP Related Exceptions
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse<?>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String message = "Invalid JSON request";
        Throwable cause = ex.getCause();

        if (cause instanceof UnrecognizedPropertyException unrecognizedEx) {
            message = String.format("Unrecognized field: '%s'. Please check your request body for typos.",
                    unrecognizedEx.getPropertyName());
        } else if (cause != null) {
            message = "Malformed JSON request: " + cause.getMessage();
        }

        return new ResponseEntity<>(
                BaseResponse.error(HttpStatus.BAD_REQUEST, message),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<BaseResponse<?>> handleNotFoundException(Exception ex) {
        String message = ex instanceof NoHandlerFoundException ?
                "Endpoint not found" : "Resource not found";

        return new ResponseEntity<>(
                BaseResponse.error(HttpStatus.NOT_FOUND, message),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<BaseResponse<?>> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        return new ResponseEntity<>(
                BaseResponse.error(HttpStatus.METHOD_NOT_ALLOWED,
                        String.format("HTTP method '%s' is not supported for this endpoint", ex.getMethod())),
                HttpStatus.METHOD_NOT_ALLOWED
        );
    }

    // Helper method to convert custom codes to HttpStatus
    private HttpStatus status(int code) {
        // Implement your custom code to HttpStatus mapping logic here
        return HttpStatus.valueOf(code);
    }
}