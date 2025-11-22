package com.cymark.estatemanagementsystem.model.dto;

import lombok.Data;

import java.math.BigDecimal;

import java.time.LocalDateTime;

@Data
public class UserTransactionStatistics {
    private LocalDateTime lastPaid;
    private BigDecimal totalAmountPaid;
}
