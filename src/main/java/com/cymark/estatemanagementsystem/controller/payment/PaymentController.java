package com.cymark.estatemanagementsystem.controller.payment;

import com.cymark.estatemanagementsystem.model.dto.VerificationData;
import com.cymark.estatemanagementsystem.model.dto.request.InitializeTransactionRequest;
import com.cymark.estatemanagementsystem.model.dto.response.InitializeTransactionResponse;
import com.cymark.estatemanagementsystem.model.dto.response.PaymentVerificationResponse;
import com.cymark.estatemanagementsystem.model.response.BaseResponse;
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
//    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<BaseResponse<InitializeTransactionResponse>> initializePayment(@RequestBody InitializeTransactionRequest paymentRequest) {
//        InitializeTransactionResponse response = new InitializeTransactionResponse();
//        response.setStatus(true);
//        response.setMessage("Successfully initialized payment");
//        VerificationData data = new VerificationData();
////        data.setAuthorizationUrl("https://api.paystack.co/transaction/verify/155365a1-c6c1-11f0-8b8e-052a6f4cfd5a");
//        data.setAuthorizationUrl("https://checkout.paystack.com/w01nrieylwuu10h");
//        data.setReference("155365a1-c6c1-11f0-8b8e-052a6f4cfd5a");
//        data.setAccessCode("w01nrieylwuu10h");
//        response.setData(data);
        return ResponseEntity.ok(BaseResponse.success(paymentService.initTransaction(paymentRequest), "Payment successfully initialized"));



//        return ResponseEntity.ok(BaseResponse.success(response, "Payment successfully initialized"));
    }

    @PostMapping( "/verify-payment")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<PaymentVerificationResponse> verifyPayment(@RequestParam(required = false, name = "reference") String paymentReference) {
        return ResponseEntity.ok(paymentService.paymentVerification(paymentReference));

    }

}

