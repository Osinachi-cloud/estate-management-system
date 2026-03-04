package com.cymark.estatemanagementsystem.model.dto.request;

import lombok.Data;

@Data
public class PasswordResetRequest {

    private String email;
    private String resetCode;
    private String password;
    private String confirmPassword;
}
