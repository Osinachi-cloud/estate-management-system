package com.cymark.estatemanagementsystem.service.impl;

import com.cymark.estatemanagementsystem.service.NotificationService;
import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender mailSender;

    @Value("${twilio.account-sid}")
    private String twilioAccountSid;

    @Value("${twilio.auth-token}")
    private String twilioAuthToken;

    @Value("${twilio.phone-number}")
    private String twilioPhoneNumber;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.otp.sms.template:Your verification code is: %s. Valid for 10 minutes.}")
    private String defaultSmsTemplate;

    @Value("${app.otp.email.subject:Your Verification Code}")
    private String defaultEmailSubject;

    @Value("${app.otp.email.template:}" +
            "<html>" +
            "<body>" +
            "<h2>Your Verification Code</h2>" +
            "<p>Your OTP code is: <strong>%s</strong></p>" +
            "<p>This code is valid for 10 minutes.</p>" +
            "<p>If you didn't request this code, please ignore this email.</p>" +
            "</body>" +
            "</html>")
    private String defaultEmailTemplate;

    @Override
    public void sendOtpToPhone(String phoneNumber, String otpCode) {
        sendOtpToPhone(phoneNumber, otpCode, defaultSmsTemplate);
    }

    @Override
    public void sendOtpToEmail(String emailAddress, String otpCode) {
        sendOtpToEmail(emailAddress, otpCode, defaultEmailSubject);
    }

    @Override
    public void sendOtpToPhone(String phoneNumber, String otpCode, String messageTemplate) {
        try {
            // Initialize Twilio
            Twilio.init(twilioAccountSid, twilioAuthToken);

            String formattedMessage = String.format(messageTemplate, otpCode);

            Message message = Message.creator(
                    new PhoneNumber(phoneNumber),
                    new PhoneNumber(twilioPhoneNumber),
                    formattedMessage
            ).create();

            log.info("OTP sent to phone: {}. Message SID: {}", phoneNumber, message.getSid());

        } catch (Exception e) {
            log.error("Failed to send OTP to phone: {}", phoneNumber, e);
            throw new RuntimeException("Failed to send SMS OTP", e);
        }
    }

    public void sendOtp(String toPhoneNumber, String otp) {
        String messageBody = "Your OTP code is: " + otp;
        Message message = Message.creator(
                        new PhoneNumber(toPhoneNumber),
                        new PhoneNumber(twilioPhoneNumber),
                        messageBody)
                .create();
        log.info("Sent message SID: {}", message.getSid());
    }

//    @PostConstruct
//    public void init() {
//        System.out.println("hi twilio 1");
//        sendOtp("+2348108040995", "124605");
//        System.out.println("hi twilio 2");
//    }

    @Override
    public void sendOtpToEmail(String emailAddress, String otpCode, String subjectTemplate) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            String formattedSubject = String.format(subjectTemplate, otpCode);
            String formattedContent = String.format(defaultEmailTemplate, otpCode);

            helper.setFrom(fromEmail);
            helper.setTo(emailAddress);
            helper.setSubject(formattedSubject);
            helper.setText(formattedContent, true); // true indicates HTML content

            mailSender.send(mimeMessage);

            log.info("OTP sent to email: {}", emailAddress);

        } catch (MessagingException e) {
            log.error("Failed to send OTP email to: {}", emailAddress, e);
            throw new RuntimeException("Failed to send email OTP", e);
        }
    }

    // Optional: Async methods for better performance
    @Async
    public void sendOtpToPhoneAsync(String phoneNumber, String otpCode) {
        sendOtpToPhone(phoneNumber, otpCode);
    }

    @Async
    public void sendOtpToEmailAsync(String emailAddress, String otpCode) {
        sendOtpToEmail(emailAddress, otpCode);
    }
}