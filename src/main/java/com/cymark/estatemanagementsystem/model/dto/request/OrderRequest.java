package com.cymark.estatemanagementsystem.model.dto.request;

import com.cymark.estatemanagementsystem.model.enums.Currency;
import com.cymark.estatemanagementsystem.model.enums.Designation;
import com.cymark.estatemanagementsystem.model.enums.PaymentMode;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.math.BigDecimal;

@Data

public class OrderRequest {

    private String orderId;

    private String transactionId;

    private BigDecimal quantity;

    private BigDecimal amount;

    private String emailAddress;

    private String productId;

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private String status;

    private String estateId;

    private String designation;

    private String productName;

    private String subscribeFor;
}
