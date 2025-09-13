package com.cymark.estatemanagementsystem.model.response;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class BaseResponse <T>{
    private String message;
    private HttpStatus status;
    private int statusCode;
    private String error;
    private LocalDateTime timestamp;
    private T data;

}
