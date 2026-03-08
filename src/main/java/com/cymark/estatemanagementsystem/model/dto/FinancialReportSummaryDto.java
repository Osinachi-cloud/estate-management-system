package com.cymark.estatemanagementsystem.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FinancialReportSummaryDto {

    // Revenue totals
    private BigDecimal totalCompletedRevenue;
    private BigDecimal totalFailedAmount;
    private BigDecimal totalProcessingAmount;

    // Transaction counts
    private Long totalTransactionCount;
    private Long completedTransactionCount;
    private Long failedTransactionCount;
    private Long processingTransactionCount;
    private Long rejectedTransactionCount;

    // Averages
    private BigDecimal averageTransactionAmount;

    // Metadata
    private String dateRangeLabel;
    private String generatedAt;

    // Breakdowns
    private List<ProductBreakdownItem> productBreakdown;
    private List<DesignationBreakdownItem> designationBreakdown;
    private List<MonthlyTrendItem> monthlyTrend;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductBreakdownItem {
        private String productName;
        private Long transactionCount;
        private BigDecimal completedRevenue;
        private BigDecimal failedAmount;
        private BigDecimal processingAmount;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DesignationBreakdownItem {
        private String designation;
        private Long transactionCount;
        private BigDecimal completedRevenue;
        private BigDecimal failedAmount;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthlyTrendItem {
        private String month;          // e.g. "2025-01"
        private String monthLabel;     // e.g. "Jan 2025"
        private Long transactionCount;
        private BigDecimal completedRevenue;
        private BigDecimal failedAmount;
        private BigDecimal processingAmount;
    }
}
