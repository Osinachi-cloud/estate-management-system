package com.cymark.estatemanagementsystem.model.response;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private String message;
    private String error;
    private int statusCode;
    private LocalDateTime timestamp;
    private Map<String, String> details;
    private String path;

    // Factory methods for common error scenarios
    public static ErrorResponse of(HttpStatus status, String message) {
        return ErrorResponse.builder()
                .message(message)
                .error(status.getReasonPhrase())
                .statusCode(status.value())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorResponse of(HttpStatus status, String message, Map<String, String> details) {
        return ErrorResponse.builder()
                .message(message)
                .error(status.getReasonPhrase())
                .statusCode(status.value())
                .timestamp(LocalDateTime.now())
                .details(details)
                .build();
    }

    public static ErrorResponse of(HttpStatus status, String message, String path) {
        return ErrorResponse.builder()
                .message(message)
                .error(status.getReasonPhrase())
                .statusCode(status.value())
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }

    public static ErrorResponse validationError(Map<String, String> fieldErrors) {
        return ErrorResponse.builder()
                .message("Validation failed")
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .details(fieldErrors)
                .build();
    }
}