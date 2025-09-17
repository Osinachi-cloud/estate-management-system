package com.cymark.estatemanagementsystem.service;

import com.cymark.estatemanagementsystem.exception.ContactVerificationException;
import com.cymark.estatemanagementsystem.model.dto.request.EmailVerificationRequest;
import com.cymark.estatemanagementsystem.model.dto.response.VerificationResponse;

public interface ContactVerificationService {

    /**
     * Adds the email address contained in the request for verification.
     * Each contact added should be unique even if the user fails to verify previous requests
     * To prevent duplicated contacts, the contact to be added should first be checked for existence
     * @param emailAddress the object containing the email address to be verified
     * @throws ContactVerificationException if error occurs adding the contact
     */
    VerificationResponse addEmailAddressForVerification(String emailAddress) throws ContactVerificationException;


    /**
     * Verifies the email address contained in the given request object.
     * @param emailVerificationRequest the email address
     * @return the verification response object
     * @throws ContactVerificationException if an error occurs verifying the email
     */
    VerificationResponse verifyEmailAddress(EmailVerificationRequest emailVerificationRequest) throws ContactVerificationException;

}
