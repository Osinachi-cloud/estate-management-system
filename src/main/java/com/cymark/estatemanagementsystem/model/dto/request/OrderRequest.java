package com.cymark.estatemanagementsystem.model.dto.request;

import com.cymark.estatemanagementsystem.model.enums.Currency;
import com.cymark.estatemanagementsystem.model.enums.PaymentMode;
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

    private String productCategoryName;

    private String vendorEmailAddress;

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private String status;

    private String narration;

    private String sleeveType;

    private String color;

    private String bodyMeasurementTag;

}
