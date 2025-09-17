package com.cymark.estatemanagementsystem.service;

import com.cymark.estatemanagementsystem.model.dto.request.PasswordResetRequest;
import com.cymark.estatemanagementsystem.model.response.Response;

public interface PasswordService {


    Response requestPasswordReset(String emailAddress);

    Response resetPassword(PasswordResetRequest passwordResetRequest);

    String encode(String password);

    void validateNewPassword(PasswordResetRequest passwordResetRequest);

    void validateNewPassword(String password);

    Response validatePasswordResetCode(PasswordResetRequest passwordResetRequest);

    boolean passwordMatch(String rawPassword, String encrypted);
}
