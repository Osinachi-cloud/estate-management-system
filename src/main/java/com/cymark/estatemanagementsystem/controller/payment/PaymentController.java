package com.cymark.estatemanagementsystem.controller.payment;

import com.cymark.estatemanagementsystem.model.dto.VerificationData;
import com.cymark.estatemanagementsystem.model.dto.request.InitializeTransactionRequest;
import com.cymark.estatemanagementsystem.model.dto.response.InitializeTransactionResponse;
import com.cymark.estatemanagementsystem.model.dto.response.PaymentVerificationResponse;
import com.cymark.estatemanagementsystem.model.entity.Transaction;
import com.cymark.estatemanagementsystem.model.response.BaseResponse;
import com.cymark.estatemanagementsystem.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import static com.cymark.estatemanagementsystem.util.Constants.BASE_URL;


@RestController
@RequestMapping(BASE_URL)
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/initialize-payment")
//    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<BaseResponse<InitializeTransactionResponse>> initializePayment(@RequestBody InitializeTransactionRequest paymentRequest) {
        log.info("Initialize payment request: {}", paymentRequest);
        Transaction transaction = new Transaction();
        transaction.setTransactionCharge(paymentRequest.getTransaction_charge());
        BigDecimal totalAMount = paymentRequest.getPrice().add(paymentRequest.getTransaction_charge()).multiply(paymentRequest.getQuantity());
        transaction.setAmount(totalAMount);
        transaction.setSingleProductFee(paymentRequest.getPrice());
        transaction.setTotalProductFee(paymentRequest.getPrice().multiply(paymentRequest.getQuantity()));
        transaction.setTotalTransactionCharge(paymentRequest.getTransaction_charge().multiply(paymentRequest.getQuantity()));
        paymentRequest.setAmount(totalAMount);
        return ResponseEntity.ok(BaseResponse.success(paymentService.initTransaction(paymentRequest, transaction), "Payment successfully initialized"));
    }

    @PostMapping( "/verify-payment")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<PaymentVerificationResponse> verifyPayment(@RequestParam(required = false, name = "reference") String paymentReference) {
        Transaction transaction = new Transaction();
        return ResponseEntity.ok(paymentService.paymentVerification(paymentReference, transaction));
    }

}

