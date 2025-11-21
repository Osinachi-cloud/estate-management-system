package com.cymark.estatemanagementsystem.model.response;

import com.cymark.estatemanagementsystem.model.dto.CustomerDto;

import com.cymark.estatemanagementsystem.model.dto.RoleDto;
import com.cymark.estatemanagementsystem.security.model.Token;
import lombok.Data;

@Data
public class LoginResponse {
    private String customerId;
    private String vendorId;
    private String tier;
    private String country;
    private String password;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String phoneNumber;
    private boolean hasPin;
//    private String role;
    private RoleDto roleDto;
    private boolean saveCard;
    private boolean enablePush;
    private String accessToken;
    private String refreshToken;
    private String profileImage;
    private String designation;

    public LoginResponse(CustomerDto customer, Token token) {
//        this.customerId = customer.getUserId();
        this.tier = customer.getTier();
        this.country = customer.getCountry();
        this.firstName = customer.getFirstName();
        this.lastName = customer.getLastName();
        this.emailAddress = customer.getEmailAddress();
        this.phoneNumber = customer.getPhoneNumber();
        this.hasPin = customer.isHasPin();
        this.saveCard = customer.isSaveCard();
        this.enablePush = customer.isEnablePush();
//        this.role = customer.getRole().getName();
        this.roleDto = customer.getRole();
        this.accessToken = token.getAccessToken();
        this.refreshToken = token.getRefreshToken();
        this.designation = customer.getDesignation();
        this.profileImage = customer.getProfileImage();
    }
}
