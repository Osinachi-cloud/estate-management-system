package com.cymark.estatemanagementsystem.service;


import com.cymark.estatemanagementsystem.model.dto.*;
import com.cymark.estatemanagementsystem.model.dto.request.CustomerRequest;
import com.cymark.estatemanagementsystem.model.dto.request.CustomerUpdateRequest;
import com.cymark.estatemanagementsystem.model.dto.request.PasswordResetRequest;
import com.cymark.estatemanagementsystem.model.entity.UserEntity;
import com.cymark.estatemanagementsystem.model.request.AdminCustomerRequest;
import com.cymark.estatemanagementsystem.model.response.PaginatedResponse;
import com.cymark.estatemanagementsystem.model.response.Response;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService {


    CustomerDto createCustomer(CustomerRequest customerRequest);

    @Transactional
    CustomerDto adminCreateCustomer(AdminCustomerRequest customerRequest);

    CustomerDto updateCustomer(CustomerUpdateRequest customerRequest, String emailAddress);

    Response updateCustomerProfileImage(String profileImage, String emailAddress);

    CustomerDto getCustomer(String customerId);

    UserEntity getCustomerEntity(String customerId);

    CustomerDto getCustomerByEmail(String emailAddress);

    void updateLastLogin(CustomerDto user);

    void updateLoginAttempts(String emailAddress);

    Response requestPasswordReset(String emailAddress);

    Response resetPassword(PasswordResetRequest passwordResetRequest);

    Response validatePasswordResetCode(PasswordResetRequest passwordResetRequest);

    Response createPin(String customerId, String pin);

    Response checkPin(String customerId, String pin);

    Response resetPinInitiateEmail(String customerId, String phoneNumber);

    Response verifyResetPinCode(String customerId, String code);

    Response resetPin(String customerId, String pin);

    Response allowSaveCard(String customerId, Boolean saveCard);

    PaginatedResponse<List<UserDto>> fetchAllUsersBy(int page, int size, String firstName, String lastName, String email, Long roleId);

    Response toggleEnableUser(String phone);
}
