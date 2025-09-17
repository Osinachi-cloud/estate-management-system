package com.cymark.estatemanagementsystem.model.dto.request;

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
