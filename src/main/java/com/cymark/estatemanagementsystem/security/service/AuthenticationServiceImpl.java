package com.cymark.estatemanagementsystem.security.service;

import com.cymark.estatemanagementsystem.exception.CymarkException;
import com.cymark.estatemanagementsystem.model.dto.CustomerDto;
import com.cymark.estatemanagementsystem.model.request.LoginRequest;
import com.cymark.estatemanagementsystem.model.response.LoginResponse;
import com.cymark.estatemanagementsystem.security.model.CustomUserDetails;
import com.cymark.estatemanagementsystem.security.model.Token;
import com.cymark.estatemanagementsystem.security.util.TokenUtils;
import com.cymark.estatemanagementsystem.service.PasswordService;
import com.cymark.estatemanagementsystem.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;


    private final TokenUtils tokenUtils;

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, UserService userService, PasswordService passwordService, TokenUtils tokenUtils) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.tokenUtils = tokenUtils;
    }

    @Override
    public LoginResponse authenticate(LoginRequest loginRequest) {
        log.info("Login request: {}", loginRequest);

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            CustomerDto user = getUser(authentication);

            Token token = tokenUtils.generateAccessAndRefreshToken(user);

            LoginResponse loginResponse = new LoginResponse(user, token);

            onSuccessfulAuthentication(user);
            return loginResponse;
        } catch (BadCredentialsException e) {
            log.error("Bad login credentials for user : {} : {}", loginRequest.getEmail(), e.getMessage());
            onFailedAuthentication(loginRequest.getEmail());
            throw new BadCredentialsException("Incorrect email address or password");
        } catch (AuthenticationException e) {
            log.error("Authentication error for user: {} : {}", loginRequest.getEmail(), e.getMessage());
            onFailedAuthentication(loginRequest.getEmail());
            if (e.getCause() != null) {
                Throwable cause = e.getCause();
                if (cause.getCause() != null) {
                    Throwable initialCause = cause.getCause();
                    throw new CymarkException(initialCause.getMessage() != null ? initialCause.getMessage() : "Error processing request");
                }
                throw new CymarkException(cause.getMessage() != null ? cause.getMessage() : "Error processing request");
            }
            throw new CymarkException(e.getMessage() != null ? e.getMessage() : "Error processing request");
        }
    }

    private void onFailedAuthentication(String emailAddress) {
        userService.updateLoginAttempts(emailAddress);
    }

    private void onSuccessfulAuthentication(CustomerDto user) {
        log.debug("Successful authentication for user: {}", user.getPhoneNumber());
        userService.updateLastLogin(user);
    }



    @Override
    public Token refreshAccessToken(String refreshToken) {

        Claims claims = tokenUtils.validateRefreshToken(refreshToken);
        log.debug("Refresh token: {}", claims);
        CustomerDto customer = userService.getCustomer(claims.getSubject());
        return tokenUtils.generateAccessAndRefreshToken(customer);
    }

    private CustomerDto getUser(Authentication authentication) {
        log.info("Get user ===>: {}", authentication.getName());
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUser();
    }

    @Override
    public CustomerDto getAuthenticatedUser() {
        if (SecurityContextHolder.getContext() != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                return getUser(authentication);
            }
        }
        throw new AccessDeniedException("Unauthenticated user");
    }

    @Override
    public String getAuthenticatedCustomerId() {
        return getAuthenticatedUser().getPhoneNumber();
    }

}

