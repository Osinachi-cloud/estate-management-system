package com.cymark.estatemanagementsystem.exception;

public class CymarkException extends RuntimeException{

    public CymarkException() {
        super("Error processing request at the moment.");
    }

    public CymarkException(String message) {
        super(message);
    }

    public CymarkException(String message, Throwable cause) {
        super(message, cause);
    }

    public CymarkException(Throwable cause) {
        super(cause);
    }

    public CymarkException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
