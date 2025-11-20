package com.cymark.estatemanagementsystem.controller.payment;

import com.cymark.estatemanagementsystem.model.dto.request.InitializeTransactionRequest;
import com.cymark.estatemanagementsystem.model.dto.response.InitializeTransactionResponse;
import com.cymark.estatemanagementsystem.model.dto.response.PaymentVerificationResponse;
import com.cymark.estatemanagementsystem.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<InitializeTransactionResponse> initializePayment(@RequestBody InitializeTransactionRequest paymentRequest) {
        return ResponseEntity.ok(paymentService.initTransaction(paymentRequest));
    }

    @PostMapping( "/verify-payment")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<PaymentVerificationResponse> verifyPayment(@RequestParam(required = false, name = "reference") String paymentReference) {
        return ResponseEntity.ok(paymentService.paymentVerification(paymentReference));

    }

}

