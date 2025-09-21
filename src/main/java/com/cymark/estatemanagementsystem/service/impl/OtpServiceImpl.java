package com.cymark.estatemanagementsystem.service.impl;


import com.cymark.estatemanagementsystem.exception.*;
import com.cymark.estatemanagementsystem.model.entity.*;
import com.cymark.estatemanagementsystem.model.enums.OTType;
import com.cymark.estatemanagementsystem.model.enums.ResponseStatus;
import com.cymark.estatemanagementsystem.model.response.Response;
import com.cymark.estatemanagementsystem.repository.OtpRepository;
import com.cymark.estatemanagementsystem.repository.UserRepository;
import com.cymark.estatemanagementsystem.service.OtpService;
import com.cymark.estatemanagementsystem.util.NumberUtils;
import com.cymark.estatemanagementsystem.util.ResponseUtils;
import com.cymark.estatemanagementsystem.util.UserValidationUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private final OtpRepository otpRepository;
    private final UserRepository userRepository;

    @Override
    public Response generateOtp(String emailAddress) {

        log.debug("Request for password reset from customer with email address [{}]", emailAddress);

        if (!UserValidationUtils.isValidEmail(emailAddress)) {
            log.warn("Customer email address [{}] invalid for password reset", emailAddress);
            throw new PasswordException(ResponseStatus.INVALID_EMAIL_ADDRESS);
        }

        Optional<UserEntity> optionalCustomer = userRepository.findByEmail(emailAddress);

        if (optionalCustomer.isEmpty()) {
            log.error("A customer with email address [{}] not found for password reset", emailAddress);
            throw new PasswordException(ResponseStatus.USER_NOT_FOUND);
        }

        try {
            Otp otp = new Otp();
            otp.setEmailAddress(emailAddress);

            String token = NumberUtils.generate(5);

            otp.setOtpCode(token);
            otp.setGeneratedOn(Instant.now());
            otp.setOTPType(OTType.SIGN_UP);
            otp.setExpiredOn(Instant.now().plus(15, ChronoUnit.MINUTES));
            otpRepository.save(otp);

            log.info("Customer has requested a password reset process and reset code sent to email [{}]", emailAddress);
            return ResponseUtils.createSuccessResponse("otp sent to your email");

        } catch (Exception e) {
            log.error("Failed to request password reset for email address [{}]", emailAddress, e);
            throw new PasswordException(ResponseStatus.PROCESSING_ERROR);
        }
    }

    @PostConstruct
    public void init() {
        System.out.println("try 1");
        generateOtp("app_owner@ems.com");
        System.out.println("try 2");
    }
}
