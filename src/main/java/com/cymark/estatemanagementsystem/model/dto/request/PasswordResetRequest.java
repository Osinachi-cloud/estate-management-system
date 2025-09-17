package com.cymark.estatemanagementsystem.model.dto.request;

import lombok.Data;

@Data
public class PasswordResetRequest {

    private String emailAddress;
    private String resetCode;
    private String newPassword;
    private String confirmPassword;
}
