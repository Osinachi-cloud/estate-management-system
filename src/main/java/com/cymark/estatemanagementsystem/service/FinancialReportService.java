package com.cymark.estatemanagementsystem.service;

import com.cymark.estatemanagementsystem.model.dto.FinancialReportSummaryDto;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface FinancialReportService {

    /**
     * Returns a comprehensive JSON summary of financial metrics for the given filters.
     */
    FinancialReportSummaryDto getFinancialReportSummary(
            String estateId,
            String fromDate,
            String toDate,
            String status,
            String designation,
            String productName,
            String userId
    );

    /**
     * Streams a multi-sheet Excel (.xlsx) financial report to the HTTP response.
     * Sheets: Summary | Transactions | Product Breakdown | Designation Breakdown | Monthly Trend
     */
    void exportFinancialReportToExcel(
            HttpServletResponse response,
            String estateId,
            String fromDate,
            String toDate,
            String status,
            String designation,
            String productName,
            String userId
    ) throws IOException;
}
