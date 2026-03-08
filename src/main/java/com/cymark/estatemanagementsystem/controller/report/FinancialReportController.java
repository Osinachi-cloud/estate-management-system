package com.cymark.estatemanagementsystem.controller.report;

import com.cymark.estatemanagementsystem.model.dto.FinancialReportSummaryDto;
import com.cymark.estatemanagementsystem.model.response.BaseResponse;
import com.cymark.estatemanagementsystem.security.model.Unsecured;
import com.cymark.estatemanagementsystem.service.FinancialReportService;
import com.cymark.estatemanagementsystem.util.DateUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.cymark.estatemanagementsystem.util.Constants.BASE_URL;

@Slf4j
@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class FinancialReportController {

    private final FinancialReportService financialReportService;

    /**
     * Returns a comprehensive JSON summary of financial metrics for the given filters.
     * Defaults to year-to-date range when dates are not provided.
     */
    @Unsecured
    @GetMapping("/get-financial-report-summary")
    public ResponseEntity<BaseResponse<FinancialReportSummaryDto>> getFinancialReportSummary(
            @RequestParam(required = false) String estateId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String userId) {

        try {
            String effectiveFromDate = (fromDate != null && !fromDate.isEmpty())
                    ? fromDate : DateUtils.getDefaultFromDateAsString();
            String effectiveToDate = (toDate != null && !toDate.isEmpty())
                    ? toDate : DateUtils.getDefaultToDateAsString();

            log.info("Fetching financial report summary: estateId={}, from={}, to={}",
                    estateId, effectiveFromDate, effectiveToDate);

            FinancialReportSummaryDto summary = financialReportService.getFinancialReportSummary(
                    estateId, effectiveFromDate, effectiveToDate,
                    status, designation, productName, userId);

            return ResponseEntity.ok(BaseResponse.success(summary,
                    "Financial report summary retrieved successfully"));

        } catch (Exception e) {
            log.error("Error retrieving financial report summary", e);
            return ResponseEntity.internalServerError()
                    .body(BaseResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Error retrieving financial report summary: " + e.getMessage()));
        }
    }

    /**
     * Streams a multi-sheet Excel (.xlsx) financial report to the HTTP response.
     * Sheets: Summary | Transactions | Product Breakdown | Designation Breakdown | Monthly Trend
     * Defaults to year-to-date range when dates are not provided.
     */
    @Unsecured
    @GetMapping("/export-financial-report")
    public void exportFinancialReport(
            HttpServletResponse response,
            @RequestParam(required = false) String estateId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String userId) throws IOException {

        String effectiveFromDate = (fromDate != null && !fromDate.isEmpty())
                ? fromDate : DateUtils.getDefaultFromDateAsString();
        String effectiveToDate = (toDate != null && !toDate.isEmpty())
                ? toDate : DateUtils.getDefaultToDateAsString();

        log.info("Exporting financial report: estateId={}, from={}, to={}, status={}, designation={}, product={}, userId={}",
                estateId, effectiveFromDate, effectiveToDate, status, designation, productName, userId);

        financialReportService.exportFinancialReportToExcel(
                response, estateId, effectiveFromDate, effectiveToDate,
                status, designation, productName, userId);
    }
}
