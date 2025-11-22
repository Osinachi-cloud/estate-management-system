package com.cymark.estatemanagementsystem.model.entity;

import com.cymark.estatemanagementsystem.model.enums.PaymentMode;
import com.cymark.estatemanagementsystem.model.enums.TransactionStatus;
import com.cymark.estatemanagementsystem.model.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transaction")
@ToString
public class Transaction extends com.cymark.estatemanagementsystem.model.entity.BaseEntity {

    @Column(name = "reference")
    private String reference;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "gateway_response")
    private String gatewayResponse;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "channel")
    private String channel;

    @Column(name = "currency")
    private String currency;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "transaction_id", unique = true, nullable = false)
    private String transactionId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "payment_card_id")
    private String paymentCardId;

    @Column(name = "card_transaction_id")
    private String cardTransactionId;

    @Column(name = "payment_mode")
    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    @Column(name = "fee")
    private BigDecimal fee;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "narration")
    private String narration;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(name = "transaction_type")
    @Enumerated(EnumType.ORDINAL)
    private TransactionType transactionType;

    @Column(name = "subscribe_start")
    private LocalDateTime subscribeFrom;

    @Column(name = "subscribe_end")
    private LocalDateTime subscribeTo;

    @Column(name = "transaction_charge")
    private BigDecimal transactionCharge;

    @Column(name = "total_transaction_charge")
    private BigDecimal totalTransactionCharge;

    @Column(name= "total_product_fee")
    private BigDecimal totalProductFee;

    @Column(name= "single_product_fee")
    private BigDecimal singleProductFee;
}
