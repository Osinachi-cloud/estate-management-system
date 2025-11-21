package com.cymark.estatemanagementsystem.model.entity;

import com.cymark.estatemanagementsystem.model.enums.Currency;
import com.cymark.estatemanagementsystem.model.enums.Designation;
import com.cymark.estatemanagementsystem.model.enums.OrderStatus;
import com.cymark.estatemanagementsystem.model.enums.PaymentMode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "order")
public class Order extends BaseEntity {

    @Column(name = "order_id", unique = true, nullable = false)
    private String orderId;

    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    @Column(name = "email_address", nullable = false)
    private String emailAddress;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "product_name")
    private String productName;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode")
    private PaymentMode paymentMode;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency")
    private Currency currency;

    @Column(name = "amount")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @Column(name = "quantity")
    private BigDecimal quantity;

    @Column(name = "estateId")
    private String estateId;

    @Column(name = "designation")
    @Enumerated(EnumType.STRING)
    private Designation designation;

    @Column(name ="subscribe_for")
    private LocalDateTime subscribeFor;

}
