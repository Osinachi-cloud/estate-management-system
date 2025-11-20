package com.cymark.estatemanagementsystem.controller.user;

import com.cymark.estatemanagementsystem.model.dto.CustomerDto;
import com.cymark.estatemanagementsystem.model.dto.request.CustomerRequest;
import com.cymark.estatemanagementsystem.model.dto.request.PasswordResetRequest;
import com.cymark.estatemanagementsystem.model.request.AdminCustomerRequest;
import com.cymark.estatemanagementsystem.model.request.LoginRequest;
import com.cymark.estatemanagementsystem.model.response.BaseResponse;
import com.cymark.estatemanagementsystem.model.response.LoginResponse;
import com.cymark.estatemanagementsystem.model.response.Response;
import com.cymark.estatemanagementsystem.security.model.Token;
import com.cymark.estatemanagementsystem.security.model.Unsecured;
import com.cymark.estatemanagementsystem.security.service.AuthenticationService;
import com.cymark.estatemanagementsystem.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.cymark.estatemanagementsystem.util.Constants.BASE_URL;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(BASE_URL)
@CrossOrigin(origins = "*")
//@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    @Unsecured
    @PostMapping("/create-customer")
    public ResponseEntity<BaseResponse<CustomerDto>> createCustomer(@RequestBody @Valid CustomerRequest customerRequest) {
        CustomerDto customer = userService.createCustomer(customerRequest);
        return new ResponseEntity<>(BaseResponse.success(customer, "Customer created successfully"), CREATED);
    }

    @Unsecured
    @PostMapping("/customer-login")
    public ResponseEntity<BaseResponse<LoginResponse>> customerLogin(@RequestBody @Valid LoginRequest loginRequest) {
        LoginResponse loginResponse = authenticationService.authenticate(loginRequest);
        return ResponseEntity.ok(BaseResponse.success(loginResponse, "You are successfully logged in."));
    }

    @Unsecured
    @PostMapping("/request-token")
    public ResponseEntity<BaseResponse<Token>> requestToken(@RequestParam("refreshToken") String refreshToken) {
        Token token = authenticationService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(BaseResponse.success(token, "Token refreshed successfully"));
    }

    @Unsecured
    @PostMapping("/request-password-reset")
    public ResponseEntity<BaseResponse<Response>> requestPasswordReset(@RequestParam("emailAddress") String emailAddress) {
        Response response = userService.requestPasswordReset(emailAddress);
        return ResponseEntity.ok(BaseResponse.success(response, "Password reset instructions sent"));
    }

    @Unsecured
    @PostMapping("/reset-password")
    public ResponseEntity<BaseResponse<Response>> resetPassword(@RequestBody @Valid PasswordResetRequest passwordResetRequest) {
        Response response = userService.resetPassword(passwordResetRequest);
        return ResponseEntity.ok(BaseResponse.success(response, "Password reset successfully"));
    }


    @Unsecured
    @PostMapping("/validate-reset-code")
    public ResponseEntity<BaseResponse<Response>> validatePasswordResetCode(@RequestBody @Valid PasswordResetRequest passwordResetRequest) {
        Response response = userService.validatePasswordResetCode(passwordResetRequest);
        return ResponseEntity.ok(BaseResponse.success(response, "Reset code validated successfully"));
    }

    @PreAuthorize("hasAuthority('CREATE_USER')")
    @PostMapping("/admin-create-customer")
    public ResponseEntity<BaseResponse<CustomerDto>> adminCreateCustomer(@RequestBody @Valid AdminCustomerRequest customerRequest) {
        CustomerDto customer = userService.adminCreateCustomer(customerRequest);
        return new ResponseEntity<>(BaseResponse.success(customer, "Customer created by admin successfully"), CREATED);
    }

//    @PostMapping("/create-pin")
//    public Response createPin(@RequestParam("pin") String pin) {
//        CustomerDto user = authenticationService.getAuthenticatedUser();
//        return userService.createPin(user.getUserId(), pin.trim());
//    }
//
//    @PostMapping("/reset-pin-initiate-email")
//    public Response resetPinInitiateEmail(@RequestParam("phoneNumber") String phoneNumber) {
//        CustomerDto user = authenticationService.getAuthenticatedUser();
//        return userService.resetPinInitiateEmail(user.getUserId(), phoneNumber.trim());
//    }
//
//    @PostMapping("/verify-reset-pin-code")
//    public Response verifyResetPinCode(@RequestParam("code") String code) {
//        CustomerDto user = authenticationService.getAuthenticatedUser();
//        return userService.verifyResetPinCode(user.getUserId(), code.trim());
//    }
//
//    @PostMapping( "/reset-pin")
//    public Response resetPin(@RequestParam("pin") String pin) {
//        CustomerDto user = authenticationService.getAuthenticatedUser();
//        return userService.resetPin(user.getUserId(), pin.trim());
//    }
}
