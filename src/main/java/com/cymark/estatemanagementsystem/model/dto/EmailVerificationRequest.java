package com.cymark.estatemanagementsystem.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailVerificationRequest {

    private String verificationCode;
    private String emailAddress;

}
