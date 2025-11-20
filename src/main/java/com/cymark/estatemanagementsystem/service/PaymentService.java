package com.cymark.estatemanagementsystem.service;

import com.cymark.estatemanagementsystem.model.dto.request.InitializeTransactionRequest;
import com.cymark.estatemanagementsystem.model.dto.response.InitializeTransactionResponse;
import com.cymark.estatemanagementsystem.model.dto.response.PaymentVerificationResponse;

public interface PaymentService {
    //    @Transactional
    InitializeTransactionResponse initTransaction(InitializeTransactionRequest request);

    //    @Transactional
    PaymentVerificationResponse paymentVerification(String reference);
}
