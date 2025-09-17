package com.cymark.estatemanagementsystem.service;

import com.cymark.estatemanagementsystem.model.response.Response;

public interface OtpService {
    Response generateOtp(String emailAddress);
}
