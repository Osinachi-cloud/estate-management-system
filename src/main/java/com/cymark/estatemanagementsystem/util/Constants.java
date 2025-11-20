package com.cymark.estatemanagementsystem.util;

import org.springframework.http.HttpStatus;

import java.util.Objects;

public final class Constants {


    public static final String FAILED = "FAILED";
    public static final String EMPTY = "";
    public static final String NIGERIA = "NIGERIA";

    private Constants() {
    }

    public static final String BASE_URL = "/api/v1";
    public static final String EMPTY_STRING = "";

    public static final String[] ALLOWED_URLS_WERE_FOUND = {
            "/altair",
            "/actuator/health",
            "/graphql",
            "/vendor/**",
            "/api/v1/create-customer",
            "/api/v1/get-users",
            "/api/v1/customer-login",
            "/api/v1/verify-email",
            "/api/v1/get-estates"
//            "/api/v1/fetch-customer-orders"
    };

    public static final String[] ALLOWED_URLS = {
            "/altair",
            "/actuator/health",
            "/graphql",
            "/vendor/**",
            "/api/v1/create-customer",
            "/api/v1/get-users",
            "/api/v1/customer-login",
            "/api/v1/verify-email",
            "/api/v1/validateEmailCode",
            "/api/v1/request-password-reset",
            "/api/v1/validate-reset-code",
            "/api/v1/get-estates"

//            "/api/v1/fetch-customer-orders"
    };

    public static HttpStatus status(int code){
        return (switch (code) {
            case 200 -> HttpStatus.OK;
            case 201 -> HttpStatus.CREATED;
            case 202 -> HttpStatus.ACCEPTED;
            case 400 -> HttpStatus.BAD_REQUEST;
            case 401 -> HttpStatus.UNAUTHORIZED;
            case 405 -> HttpStatus.CONFLICT;
            case 406 -> HttpStatus.NOT_ACCEPTABLE;
            case 417 -> HttpStatus.EXPECTATION_FAILED;
            case 503, 504  -> HttpStatus.SERVICE_UNAVAILABLE;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        });
    }

    public static String getStr(String value){
        if (Objects.isNull(value) || value.trim().isEmpty()) return EMPTY;
        return value.trim();

    }
}
