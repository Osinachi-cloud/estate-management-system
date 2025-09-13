package com.cymark.estatemanagementsystem.security.service;

import com.cymark.estatemanagementsystem.model.dto.CustomerDto;
import com.cymark.estatemanagementsystem.model.request.LoginRequest;
import com.cymark.estatemanagementsystem.model.response.LoginResponse;
import com.cymark.estatemanagementsystem.security.model.Token;

public interface AuthenticationService {

    LoginResponse authenticate(LoginRequest loginRequest);
    Token refreshAccessToken(String token);
    CustomerDto getAuthenticatedUser();
    String getAuthenticatedCustomerId();
}
