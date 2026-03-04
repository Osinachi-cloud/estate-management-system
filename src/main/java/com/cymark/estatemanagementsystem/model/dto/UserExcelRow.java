package com.cymark.estatemanagementsystem.model.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class UserExcelRow {
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String designation;
    private String fullAddress;
    private String country;
    private String nationality;
    private LocalDateTime lastPaid;
    private BigDecimal amountPaidThisYear;
    private BigDecimal outstandingDebt;
    private String landlordFullName;
    private String tenantFullNames; // For landlords - comma separated tenants
    private String occupantFullNames; // For tenants - comma separated occupants
    private boolean enabled;
    private String estateName;
    private String userId;
}