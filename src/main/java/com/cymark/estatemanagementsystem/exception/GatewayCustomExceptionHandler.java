package com.cymark.estatemanagementsystem.exception;

//import com.cymark.estatemanagementsystem.model.response.ErrorResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.validation.FieldError;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Objects;
//
//import static com.cymark.estatemanagementsystem.util.Constants.status;
//import static org.springframework.http.HttpStatus.BAD_REQUEST;
//import static org.springframework.http.HttpStatus.UNAUTHORIZED;
//
//@Slf4j
//@ControllerAdvice
//public class GatewayCustomExceptionHandler {
//
//
//    @ExceptionHandler
//    protected ResponseEntity<ErrorResponse> handleException(Throwable e) {
//        log.error(e.getMessage(), e);
//
//        ErrorResponse errorResponse = new ErrorResponse();
//        errorResponse.setCode(2);
//        errorResponse.setMessage("Error processing request");
//
//        return ResponseEntity.badRequest().body(errorResponse);
//    }
//
//    @ExceptionHandler(ApiException.class)
//    public ResponseEntity<Map<String, String>> handleApiExceptions(ApiException ex) {
//        return new ResponseEntity<>(Map.of("error", ex.getMessage()), status(ex.getCode()));
//    }
//    @ExceptionHandler(BadCredentialsException.class)
//    public ResponseEntity<Map<String, String>> handleApiExceptions(BadCredentialsException ex) {
//        return new ResponseEntity<>(Map.of("error", ex.getMessage()), UNAUTHORIZED);
//    }
//
//
////    @ExceptionHandler(PaymentException.class)
////    public ResponseEntity<Map<String, String>> handlePaymentException(PaymentException ex) {
////        return new ResponseEntity<>(Map.of("error", ex.getMessage()), status(ex.getCode()));
////    }
//
//
//    @ExceptionHandler(UserException.class)
//    public ResponseEntity<Map<String, String>> handleUserException(UserException ex) {
//        if (Objects.nonNull(ex.getStatus())){
//            return new ResponseEntity<>(Map.of("error", ex.getMessage()), ex.getStatus().getHttpStatus());
//        }
//        if (ex.getCode() >= 200){
//            return new ResponseEntity<>(Map.of("error", ex.getMessage()), status(ex.getCode()));
//        }
//        return new ResponseEntity<>(Map.of("error", ex.getMessage()), BAD_REQUEST);
//    }
//
//
//    @ExceptionHandler(CymarkException.class)
//    public ResponseEntity<Map<String, String>> handlecymarkException(CymarkException ex) {
//        return new ResponseEntity<>(Map.of("error", ex.getMessage()), BAD_REQUEST);
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<Map<String, String>> handleInvalidMethodArgument(MethodArgumentNotValidException ex) {
//        Map<String, String> errors = new HashMap<>();
//        log.error("Method arguments not valid ==> {}", ex.getMessage());
//        ex.getBindingResult().getAllErrors().forEach(error -> {
//            String fieldName = ((FieldError) error).getField();
//            String errorMessage = error.getDefaultMessage();
//            errors.put(fieldName, errorMessage);
//        });
//        return new ResponseEntity<>(errors, BAD_REQUEST);
//    }
//
//    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
//    public ResponseEntity<Map<String, String>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
//        Map<String, String> errors = new HashMap<>();
//        errors.put("parameterName", ex.getName());
//        errors.put("parameterValue", Objects.isNull(ex.getValue()) ? "" : String.valueOf(ex.getValue()));
//        errors.put("message", ex.getMessage());
//        return new ResponseEntity<>(errors, BAD_REQUEST);
//    }
//}



//package com.cymark.estatemanagementsystem.exception;

import com.cymark.estatemanagementsystem.model.response.ErrorResponse;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
//import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
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
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.cymark.estatemanagementsystem.util.Constants.status;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@ControllerAdvice
public class GatewayCustomExceptionHandler {

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleException(Throwable e) {
        log.error(e.getMessage(), e);

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(2);
        errorResponse.setMessage("Error processing request");

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, String>> handleApiExceptions(ApiException ex) {
        return new ResponseEntity<>(Map.of("error", ex.getMessage()), status(ex.getCode()));
    }

    // Authentication Exceptions (401 UNAUTHORIZED)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(BadCredentialsException ex) {
        return new ResponseEntity<>(Map.of("error", "Invalid username or password"), UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationException(AuthenticationException ex) {
        return new ResponseEntity<>(Map.of("error", "Authentication required: " + ex.getMessage()), UNAUTHORIZED);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleInsufficientAuthenticationException(InsufficientAuthenticationException ex) {
        return new ResponseEntity<>(Map.of("error", "Full authentication is required to access this resource"), UNAUTHORIZED);
    }

    // Authorization Exceptions (403 FORBIDDEN)
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        log.warn("Authorization denied: {}", ex.getMessage());
        return new ResponseEntity<>(Map.of(
                "error", "Access Denied",
                "message", "You do not have permission to access this resource"
        ), FORBIDDEN);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return new ResponseEntity<>(Map.of(
                "error", "Access Denied",
                "message", "You do not have the required permissions to perform this action"
        ), FORBIDDEN);
    }

    // JWT Specific Exceptions (401 UNAUTHORIZED)
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Map<String, String>> handleExpiredJwtException(ExpiredJwtException ex) {
        log.warn("JWT token expired: {}", ex.getMessage());
        return new ResponseEntity<>(Map.of(
                "error", "Token expired",
                "message", "Your session has expired. Please login again."
        ), UNAUTHORIZED);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<Map<String, String>> handleSignatureException(SignatureException ex) {
        log.warn("Invalid JWT signature: {}", ex.getMessage());
        return new ResponseEntity<>(Map.of(
                "error", "Invalid token signature",
                "message", "The token signature is invalid or has been tampered with."
        ), UNAUTHORIZED);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<Map<String, String>> handleMalformedJwtException(MalformedJwtException ex) {
        log.warn("Malformed JWT token: {}", ex.getMessage());
        return new ResponseEntity<>(Map.of(
                "error", "Malformed token",
                "message", "The token is malformed or incorrectly structured."
        ), UNAUTHORIZED);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, String>> handleJwtSecurityException(SecurityException ex) {
        log.warn("JWT security exception: {}", ex.getMessage());
        return new ResponseEntity<>(Map.of(
                "error", "Token security violation",
                "message", "There was a security issue with the token."
        ), UNAUTHORIZED);
    }

//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
//        // Only handle JWT-related IllegalArgumentExceptions
//        if (ex.getMessage() != null &&
//                (ex.getMessage().contains("JWT") || ex.getMessage().contains("token"))) {
//            log.warn("Illegal argument in JWT: {}", ex.getMessage());
//            return new ResponseEntity<>(Map.of(
//                    "error", "Invalid token",
//                    "message", "The token contains invalid arguments or claims."
//            ), UNAUTHORIZED);
//        }
//        // For other IllegalArgumentExceptions, let it fall through to generic handler
//        throw ex;
//    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<Map<String, String>> handleUserException(UserException ex) {
        if (Objects.nonNull(ex.getStatus())){
            return new ResponseEntity<>(Map.of("error", ex.getMessage()), ex.getStatus().getHttpStatus());
        }
        if (ex.getCode() >= 200){
            return new ResponseEntity<>(Map.of("error", ex.getMessage()), status(ex.getCode()));
        }
        return new ResponseEntity<>(Map.of("error", ex.getMessage()), BAD_REQUEST);
    }

    @ExceptionHandler(CymarkException.class)
    public ResponseEntity<Map<String, String>> handlecymarkException(CymarkException ex) {
        return new ResponseEntity<>(Map.of("error", ex.getMessage()), BAD_REQUEST);
    }

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<Map<String, String>> handleInvalidMethodArgument(MethodArgumentNotValidException ex) {
//        Map<String, String> errors = new HashMap<>();
//        log.error("Method arguments not valid ==> {}", ex.getMessage());
//        ex.getBindingResult().getAllErrors().forEach(error -> {
//            String fieldName = ((FieldError) error).getField();
//            String errorMessage = error.getDefaultMessage();
//            errors.put(fieldName, errorMessage);
//        });
//        return new ResponseEntity<>(errors, BAD_REQUEST);
//    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("parameterName", ex.getName());
        errors.put("parameterValue", Objects.isNull(ex.getValue()) ? "" : String.valueOf(ex.getValue()));
        errors.put("message", ex.getMessage());
        return new ResponseEntity<>(errors, BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        errors.put("success", false);
        errors.put("message", "Validation failed");
        errors.put("errors", fieldErrors);
        errors.put("status", HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Invalid input");
        response.put("error", ex.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Map<String, Object> response = new HashMap<>();

        String errorMessage = "Invalid JSON request";

        // Check if it's an unrecognized property error
        Throwable cause = ex.getCause();
        if (cause instanceof UnrecognizedPropertyException unrecognizedPropertyException) {
            String fieldName = unrecognizedPropertyException.getPropertyName();
            errorMessage = "Unrecognized field: '" + fieldName + "'. Please check your request body for typos.";
        } else if (cause != null) {
            errorMessage = "Malformed JSON request: " + cause.getMessage();
        }

        response.put("success", false);
        response.put("message", errorMessage);
        response.put("status", HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle 404 - No handler found for the endpoint
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Endpoint not found");
        response.put("details", "The requested endpoint '" + ex.getRequestURL() + "' does not exist");
        response.put("status", HttpStatus.NOT_FOUND.value());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Handle 404 - No static resource found (which you already encountered)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResourceFoundException(NoResourceFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Resource not found");
        response.put("details", "The requested resource '" + ex.getResourcePath() + "' was not found");
        response.put("status", HttpStatus.NOT_FOUND.value());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "HTTP method not supported");
        response.put("details", "Method '" + ex.getMethod() + "' is not supported for this endpoint");
        response.put("supportedMethods", ex.getSupportedMethods());
        response.put("status", HttpStatus.METHOD_NOT_ALLOWED.value());

        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

//    // Handle general 404 errors
//    @ExceptionHandler(ChangeSetPersister.NotFoundException.class) // You can create this custom exception
//    public ResponseEntity<Map<String, Object>> handleNotFoundException(ChangeSetPersister.NotFoundException ex) {
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", false);
//        response.put("message", ex.getMessage());
//        response.put("status", HttpStatus.NOT_FOUND.value());
//
//        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
//    }
}