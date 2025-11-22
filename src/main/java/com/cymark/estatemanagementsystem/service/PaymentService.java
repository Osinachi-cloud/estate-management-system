package com.cymark.estatemanagementsystem.service;

import com.cymark.estatemanagementsystem.model.dto.request.InitializeTransactionRequest;
import com.cymark.estatemanagementsystem.model.dto.response.InitializeTransactionResponse;
import com.cymark.estatemanagementsystem.model.dto.response.PaymentVerificationResponse;
import com.cymark.estatemanagementsystem.model.entity.Transaction;

public interface PaymentService {
    //    @Transactional
    InitializeTransactionResponse initTransaction(InitializeTransactionRequest request, Transaction transaction);

    //    @Transactional
    PaymentVerificationResponse paymentVerification(String reference, Transaction transaction);
}
