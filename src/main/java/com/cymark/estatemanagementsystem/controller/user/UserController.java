package com.cymark.estatemanagementsystem.controller.user;

import com.cymark.estatemanagementsystem.model.dto.*;
import com.cymark.estatemanagementsystem.model.dto.request.CustomerUpdateRequest;
import com.cymark.estatemanagementsystem.model.dto.request.EmailVerificationRequest;
import com.cymark.estatemanagementsystem.model.dto.response.VerificationResponse;
import com.cymark.estatemanagementsystem.model.request.ContactVerificationRequest;
import com.cymark.estatemanagementsystem.model.response.BaseResponse;
import com.cymark.estatemanagementsystem.model.response.PaginatedResponse;
import com.cymark.estatemanagementsystem.model.response.Response;
import com.cymark.estatemanagementsystem.security.model.Unsecured;
import com.cymark.estatemanagementsystem.security.service.AuthenticationService;
import com.cymark.estatemanagementsystem.service.ContactVerificationService;
import com.cymark.estatemanagementsystem.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cymark.estatemanagementsystem.util.Constants.BASE_URL;
import static org.springframework.http.HttpStatus.OK;

//@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping(BASE_URL)
public class UserController {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final ContactVerificationService verificationService;

    @PreAuthorize("hasAuthority('TOGGLE_ENABLE_USER')")
    @PostMapping("/toggle-enable-user")
    public ResponseEntity<BaseResponse<Response>> adminEnableUser(@RequestParam String phone) {
        log.info("PhoneNum : {} ", phone);
        Response response = userService.toggleEnableUser(phone);
        return new ResponseEntity<>(BaseResponse.success(response, "User status toggled successfully"), OK);
    }

    @Unsecured
    @GetMapping("/get-users")
    public ResponseEntity<BaseResponse<PaginatedResponse<List<UserDto>>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Long roleId) {

        PaginatedResponse<List<UserDto>> users = userService.fetchAllUsersBy(page, size, firstName, lastName, email, roleId);
        return ResponseEntity.ok(BaseResponse.success(users, "Users retrieved successfully"));
    }

    @PutMapping("/update-customer")
    public ResponseEntity<BaseResponse<CustomerDto>> updateCustomer(
            @RequestBody @Valid CustomerUpdateRequest customerRequest,
            @RequestParam("email") @NotBlank String emailAddress) {

        CustomerDto customer = userService.updateCustomer(customerRequest, emailAddress);
        return ResponseEntity.ok(BaseResponse.success(customer, "Customer updated successfully"));
    }

    @PostMapping("/update-customer-profile-image")
    public ResponseEntity<BaseResponse<Response>> updateCustomerProfileImage(
            @RequestParam("profileImage") @NotBlank String profileImage,
            @RequestParam("emailAddress") @NotBlank String emailAddress) {

        Response response = userService.updateCustomerProfileImage(profileImage, emailAddress);
        return ResponseEntity.ok(BaseResponse.success(response, "Profile image updated successfully"));
    }

    @GetMapping("/customer")
    public ResponseEntity<BaseResponse<CustomerDto>> getCustomer(@RequestParam("customerId") @NotBlank String customerId) {
        CustomerDto customer = userService.getCustomer(customerId);
        return ResponseEntity.ok(BaseResponse.success(customer, "Customer retrieved successfully"));
    }

    @GetMapping("/customer-details")
    public ResponseEntity<BaseResponse<CustomerDto>> getCustomerByEmailAddress(
            @RequestParam("email") @NotBlank @Email String emailAddress) {

        CustomerDto customer = userService.getCustomerByEmail(emailAddress);
        return ResponseEntity.ok(BaseResponse.success(customer, "Customer retrieved successfully"));
    }

    @Unsecured
    @PostMapping("/verify-email")
    public ResponseEntity<BaseResponse<VerificationResponse>> verifyEmail(
            @RequestBody @Valid ContactVerificationRequest contactVerification) {

        log.info("contactVerification ===>>>: {}", contactVerification);
        VerificationResponse response = verificationService.addEmailAddressForVerification(contactVerification.getEmailAddress());
        return ResponseEntity.ok(BaseResponse.success(response, "Verification code sent successfully"));
    }

    @Unsecured
    @PostMapping("/validate-email-code")
    public ResponseEntity<BaseResponse<VerificationResponse>> validateEmailCode(
            @RequestBody @Valid EmailVerificationRequest verificationRequest) {

        VerificationResponse response = verificationService.verifyEmailAddress(verificationRequest);
        return ResponseEntity.ok(BaseResponse.success(response, "Email verified successfully"));
    }

//    @PostMapping(value = "allowSaveCard")
//    public Response allowSaveCard(@RequestParam("savedCard") Boolean savedCard) {
//        CustomerDto user = authenticationService.getAuthenticatedUser();
//        return userService.allowSaveCard(user.getUserId(), savedCard);
//    }
}

