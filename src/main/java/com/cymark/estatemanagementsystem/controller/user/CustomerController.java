package com.cymark.estatemanagementsystem.controller.user;

import com.cymark.estatemanagementsystem.model.dto.*;
import com.cymark.estatemanagementsystem.model.request.AdminCustomerRequest;
import com.cymark.estatemanagementsystem.model.request.ContactVerificationRequest;
import com.cymark.estatemanagementsystem.model.response.PaginatedResponse;
import com.cymark.estatemanagementsystem.model.response.Response;
import com.cymark.estatemanagementsystem.security.model.Unsecured;
import com.cymark.estatemanagementsystem.security.service.AuthenticationService;
import com.cymark.estatemanagementsystem.service.ContactVerificationService;
import com.cymark.estatemanagementsystem.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cymark.estatemanagementsystem.util.Constants.BASE_URL;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

//@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping(BASE_URL)
public class CustomerController {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final ContactVerificationService verificationService;


    @Unsecured
    @PostMapping("/create-customer")
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody CustomerRequest customerRequest) {
        return new ResponseEntity<>(userService.createCustomer(customerRequest), CREATED);
    }

    @PreAuthorize("hasAuthority('CREATE_USER')")
    @PostMapping("/admin-create-customer")
    public ResponseEntity<CustomerDto> adminCreateCustomer(@RequestBody @Valid AdminCustomerRequest customerRequest) {
        return new ResponseEntity<>(userService.adminCreateCustomer(customerRequest), CREATED);
    }

    @PreAuthorize("hasAuthority('TOGGLE_ENABLE_USER')")
    @PostMapping("/toggle-enable-user")
    public ResponseEntity<Response> adminEnableUser(@RequestParam String phone) {
        log.info("PhoneNum : {} ",phone);
        return new ResponseEntity<>(userService.toggleEnableUser(phone), OK);
    }

    @Unsecured
    @GetMapping("/get-users")
    public ResponseEntity<PaginatedResponse<List<UserDto>>> getUsers(
        @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
         @RequestParam(required = false) String firstName, @RequestParam(required = false) String lastName,
         @RequestParam(required = false) String email, @RequestParam(required = false) Long roleId) {
            return ResponseEntity.ok(userService.fetchAllUsersBy(page, size, firstName, lastName, email, roleId));

    }

    @PutMapping("/update-customer")
    public ResponseEntity<CustomerDto> updateCustomer(@RequestBody CustomerUpdateRequest customerRequest,
                                      @RequestParam("emailAddress") String emailAddress) {
            return ResponseEntity.ok(userService.updateCustomer(customerRequest, emailAddress));

    }

    @PostMapping(value = "/update-customer-profile-image")
    public ResponseEntity<Response> updateCustomerProfileImage(@RequestParam("profileImage") String profileImage,
                                               @RequestParam("emailAddress") String emailAddress) {

        return ResponseEntity.ok(userService.updateCustomerProfileImage(profileImage, emailAddress));
    }

    @Unsecured
    @PostMapping("/request-password-reset")
    public ResponseEntity<Response> requestPasswordReset(@RequestParam("emailAddress") String emailAddress) {
        return ResponseEntity.ok(userService.requestPasswordReset(emailAddress));
    }

    @Unsecured
    @PostMapping("/reset-password")
    public ResponseEntity<Response> resetPassword(@RequestBody PasswordResetRequest passwordResetRequest) {
        return ResponseEntity.ok(userService.resetPassword(passwordResetRequest));
    }

    @Unsecured
    @PostMapping(value = "validate-reset-code")
    public ResponseEntity<Response> validatePasswordResetCode(@RequestBody PasswordResetRequest passwordResetRequest) {
        return ResponseEntity.ok(userService.validatePasswordResetCode(passwordResetRequest));
    }

    @GetMapping(value = "/customer")
    public ResponseEntity<CustomerDto> getCustomer(@RequestParam("customerId") String customerId) {
        return ResponseEntity.ok(userService.getCustomer(customerId));
    }

    @GetMapping("/customer-details")
    public ResponseEntity<CustomerDto> getCustomerByEmailAddress(@RequestParam("emailAddress") String emailAddress) {
        return ResponseEntity.ok(userService.getCustomerByEmail(emailAddress));
    }


    @Unsecured
    @PostMapping(value = "/verify-email")
    public ResponseEntity<VerificationResponse> verifyEmail(@RequestBody ContactVerificationRequest contactVerification) {
        log.info("contactVerification ===>>>: {}", contactVerification);
        return ResponseEntity.ok(verificationService.addEmailAddressForVerification(contactVerification.getEmailAddress()));
    }

    @Unsecured
    @PostMapping(value = "validateEmailCode")
    public VerificationResponse validateEmailCode(@RequestBody EmailVerificationRequest verificationRequest) {
        return verificationService.verifyEmailAddress(verificationRequest);
    }

//    @PostMapping(value = "allowSaveCard")
//    public Response allowSaveCard(@RequestParam("savedCard") Boolean savedCard) {
//        CustomerDto user = authenticationService.getAuthenticatedUser();
//        return userService.allowSaveCard(user.getUserId(), savedCard);
//    }
}

