package com.cymark.estatemanagementsystem.model.response;

import lombok.Data;

@Data
public class ErrorResponse {
    private Integer code;
    private String message;
}
