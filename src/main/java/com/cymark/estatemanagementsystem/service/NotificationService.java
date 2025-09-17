package com.cymark.estatemanagementsystem.service;

public interface NotificationService {
    void sendOtpToPhone(String phoneNumber, String otpCode);

    void sendOtpToEmail(String emailAddress, String otpCode);

    void sendOtpToPhone(String phoneNumber, String otpCode, String messageTemplate);

    void sendOtpToEmail(String emailAddress, String otpCode, String subjectTemplate);
}
