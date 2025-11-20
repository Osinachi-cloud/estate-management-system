package com.cymark.estatemanagementsystem.exception;


public class PaymentException extends CymarkException {

    final int code;

    public PaymentException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
