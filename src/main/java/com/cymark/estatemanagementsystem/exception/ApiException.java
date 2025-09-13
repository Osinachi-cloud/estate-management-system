package com.cymark.estatemanagementsystem.exception;

import lombok.Getter;

@Getter
public class ApiException extends CymarkException {
    final int code;
    public ApiException(String message, int code) {
        super(message);
        this.code = code;
    }
}
