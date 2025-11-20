package com.cymark.estatemanagementsystem.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserFinancialDetails {
    private String lastPaid;
    private String outStanding;
    private BigDecimal totalPerYear;
    private BigDecimal totalPaidPaid;
}
