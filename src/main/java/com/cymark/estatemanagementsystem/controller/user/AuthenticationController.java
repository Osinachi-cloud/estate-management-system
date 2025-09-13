package com.cymark.estatemanagementsystem.controller.user;

import com.cymark.estatemanagementsystem.model.dto.CustomerDto;
import com.cymark.estatemanagementsystem.model.request.LoginRequest;
import com.cymark.estatemanagementsystem.model.response.LoginResponse;
import com.cymark.estatemanagementsystem.model.response.Response;
import com.cymark.estatemanagementsystem.security.model.Token;
import com.cymark.estatemanagementsystem.security.model.Unsecured;
import com.cymark.estatemanagementsystem.security.service.AuthenticationService;
import com.cymark.estatemanagementsystem.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.cymark.estatemanagementsystem.util.Constants.BASE_URL;

@RestController
@RequestMapping(BASE_URL)
//@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    @Unsecured
    @PostMapping("/customer-login")
    public LoginResponse customerLogin(@RequestBody @Valid LoginRequest loginRequest) {
        return authenticationService.authenticate(loginRequest);
    }

    @Unsecured
    @PostMapping("/request-token")
    public Token requestToken(@RequestParam("refreshToken") String refreshToken) {
        return authenticationService.refreshAccessToken(refreshToken);
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
