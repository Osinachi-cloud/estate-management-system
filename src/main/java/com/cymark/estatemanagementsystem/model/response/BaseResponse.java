package com.cymark.estatemanagementsystem.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {
    private String message;
    @JsonIgnore // Don't serialize HttpStatus to JSON
    private HttpStatus status;
    private int statusCode;
    private String error;
    private LocalDateTime timestamp;
    private T data;

    // Success factory methods
    public static <T> BaseResponse<T> success(T data) {
        return BaseResponse.<T>builder()
                .data(data)
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> BaseResponse<T> success(T data, String message) {
        return BaseResponse.<T>builder()
                .data(data)
                .message(message)
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Error factory methods
    public static <T> BaseResponse<T> error(HttpStatus status, String error) {
        return BaseResponse.<T>builder()
                .error(error)
                .status(status)
                .statusCode(status.value())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> BaseResponse<T> error(HttpStatus status, ErrorResponse errorResponse) {
        return BaseResponse.<T>builder()
                .error(errorResponse.getError())
                .status(status)
                .statusCode(status.value())
                .timestamp(LocalDateTime.now())
                .build();
    }
}