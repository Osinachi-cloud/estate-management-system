package com.cymark.estatemanagementsystem.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstateTransactionStatistics {
    private BigDecimal totalSuccessfulAmountPaid;
    private BigDecimal totalFailedAmountPaid;
    private Long totalFailedTransactions;
    private Long totalSuccessfulTransactions;
}
