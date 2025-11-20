package com.cymark.estatemanagementsystem.exception;

import lombok.Getter;

@Getter
public class OrderException extends CymarkException {
    final int code;
    public OrderException(String message, int code) {
        super(message);
        this.code = code;
    }
}
