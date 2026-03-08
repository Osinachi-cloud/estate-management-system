package com.cymark.estatemanagementsystem.service.impl;

import com.cymark.estatemanagementsystem.model.dto.FinancialReportSummaryDto;
import com.cymark.estatemanagementsystem.model.entity.Transaction;
import com.cymark.estatemanagementsystem.model.enums.Designation;
import com.cymark.estatemanagementsystem.model.enums.TransactionStatus;
import com.cymark.estatemanagementsystem.repository.TransactionRepository;
import com.cymark.estatemanagementsystem.service.FinancialReportService;
import com.cymark.estatemanagementsystem.specification.TransactionSpecifications;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinancialReportServiceImpl implements FinancialReportService {

    private final TransactionRepository transactionRepository;

    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
    private static final DateTimeFormatter MONTH_KEY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final DateTimeFormatter MONTH_LABEL_FORMATTER = DateTimeFormatter.ofPattern("MMM yyyy");
    private static final DateTimeFormatter DT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // =====================================================================
    // Public API
    // =====================================================================

    @Override
    public FinancialReportSummaryDto getFinancialReportSummary(
            String estateId, String fromDate, String toDate,
            String status, String designation, String productName, String userId) {

        List<Transaction> transactions = fetchTransactions(
                estateId, fromDate, toDate, status, designation, productName, userId);
        return buildSummary(transactions, fromDate, toDate);
    }

    @Override
    public void exportFinancialReportToExcel(
            HttpServletResponse response,
            String estateId, String fromDate, String toDate,
            String status, String designation, String productName, String userId) throws IOException {

        List<Transaction> transactions = fetchTransactions(
                estateId, fromDate, toDate, status, designation, productName, userId);
        FinancialReportSummaryDto summary = buildSummary(transactions, fromDate, toDate);

        String filename = "financial_report_" + System.currentTimeMillis() + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);

        try (Workbook workbook = new XSSFWorkbook()) {
            buildSummarySheet(workbook, summary, estateId);
            buildTransactionsSheet(workbook, transactions);
            buildProductBreakdownSheet(workbook, summary.getProductBreakdown());
            buildDesignationBreakdownSheet(workbook, summary.getDesignationBreakdown());
            buildMonthlyTrendSheet(workbook, summary.getMonthlyTrend());
            workbook.write(response.getOutputStream());
        }
    }

    // =====================================================================
    // Data fetching
    // =====================================================================

    private List<Transaction> fetchTransactions(
            String estateId, String fromDate, String toDate,
            String status, String designation, String productName, String userId) {

        TransactionStatus statusEnum = parseStatus(status);
        Designation designationEnum = parseDesignation(designation);

        LocalDateTime fromDateTime = (fromDate != null && !fromDate.isEmpty())
                ? LocalDate.parse(fromDate).atStartOfDay() : null;
        LocalDateTime toDateTime = (toDate != null && !toDate.isEmpty())
                ? LocalDate.parse(toDate).atTime(LocalTime.MAX) : null;

        Specification<Transaction> spec = TransactionSpecifications.withFilters(
                null, statusEnum, designationEnum, productName, userId, estateId, fromDateTime, toDateTime);

        log.info("Fetching transactions for report: estateId={}, from={}, to={}", estateId, fromDate, toDate);
        return transactionRepository.findAll(spec);
    }

    // =====================================================================
    // Summary computation
    // =====================================================================

    private FinancialReportSummaryDto buildSummary(List<Transaction> transactions,
                                                    String fromDate, String toDate) {
        BigDecimal completedRevenue = BigDecimal.ZERO;
        BigDecimal failedAmount = BigDecimal.ZERO;
        BigDecimal processingAmount = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        long completedCount = 0, failedCount = 0, processingCount = 0, rejectedCount = 0;

        for (Transaction t : transactions) {
            BigDecimal amount = t.getAmount() != null ? t.getAmount() : BigDecimal.ZERO;
            totalAmount = totalAmount.add(amount);
            if (t.getStatus() != null) {
                switch (t.getStatus()) {
                    case COMPLETED  -> { completedRevenue  = completedRevenue.add(amount);   completedCount++; }
                    case FAILED     -> { failedAmount      = failedAmount.add(amount);       failedCount++; }
                    case PROCESSING -> { processingAmount  = processingAmount.add(amount);   processingCount++; }
                    case REJECTED   -> rejectedCount++;
                }
            }
        }

        long total = transactions.size();
        BigDecimal avg = total > 0
                ? totalAmount.divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return FinancialReportSummaryDto.builder()
                .totalCompletedRevenue(completedRevenue)
                .totalFailedAmount(failedAmount)
//                .totalProcessingAmount(processingAmount)
                .totalTransactionCount(total)
                .completedTransactionCount(completedCount)
                .failedTransactionCount(failedCount)
//                .processingTransactionCount(processingCount)
//                .rejectedTransactionCount(rejectedCount)
                .averageTransactionAmount(avg)
                .dateRangeLabel(buildDateLabel(fromDate, toDate))
                .generatedAt(LocalDateTime.now().format(DISPLAY_FORMATTER))
                .productBreakdown(buildProductBreakdown(transactions))
                .designationBreakdown(buildDesignationBreakdown(transactions))
                .monthlyTrend(buildMonthlyTrend(transactions))
                .build();
    }

    private List<FinancialReportSummaryDto.ProductBreakdownItem> buildProductBreakdown(
            List<Transaction> transactions) {
        return transactions.stream()
                .collect(Collectors.groupingBy(t ->
                        t.getProductName() != null ? t.getProductName() : "N/A"))
                .entrySet().stream()
                .map(e -> {
                    List<Transaction> list = e.getValue();
                    return FinancialReportSummaryDto.ProductBreakdownItem.builder()
                            .productName(e.getKey())
                            .transactionCount((long) list.size())
                            .completedRevenue(sumByStatus(list, TransactionStatus.COMPLETED))
                            .failedAmount(sumByStatus(list, TransactionStatus.FAILED))
                            .processingAmount(sumByStatus(list, TransactionStatus.PROCESSING))
                            .build();
                })
                .sorted(Comparator.comparing(
                        FinancialReportSummaryDto.ProductBreakdownItem::getCompletedRevenue).reversed())
                .collect(Collectors.toList());
    }

    private List<FinancialReportSummaryDto.DesignationBreakdownItem> buildDesignationBreakdown(
            List<Transaction> transactions) {
        return transactions.stream()
                .collect(Collectors.groupingBy(t ->
                        t.getDesignation() != null ? t.getDesignation().name() : "N/A"))
                .entrySet().stream()
                .map(e -> {
                    List<Transaction> list = e.getValue();
                    return FinancialReportSummaryDto.DesignationBreakdownItem.builder()
                            .designation(e.getKey())
                            .transactionCount((long) list.size())
                            .completedRevenue(sumByStatus(list, TransactionStatus.COMPLETED))
                            .failedAmount(sumByStatus(list, TransactionStatus.FAILED))
                            .build();
                })
                .sorted(Comparator.comparing(
                        FinancialReportSummaryDto.DesignationBreakdownItem::getCompletedRevenue).reversed())
                .collect(Collectors.toList());
    }

    private List<FinancialReportSummaryDto.MonthlyTrendItem> buildMonthlyTrend(
            List<Transaction> transactions) {
        return new TreeMap<>(transactions.stream()
                .filter(t -> t.getCreatedAt() != null)
                .collect(Collectors.groupingBy(t -> t.getCreatedAt().format(MONTH_KEY_FORMATTER))))
                .entrySet().stream()
                .map(e -> {
                    List<Transaction> list = e.getValue();
                    return FinancialReportSummaryDto.MonthlyTrendItem.builder()
                            .month(e.getKey())
                            .monthLabel(LocalDate.parse(e.getKey() + "-01").format(MONTH_LABEL_FORMATTER))
                            .transactionCount((long) list.size())
                            .completedRevenue(sumByStatus(list, TransactionStatus.COMPLETED))
                            .failedAmount(sumByStatus(list, TransactionStatus.FAILED))
                            .processingAmount(sumByStatus(list, TransactionStatus.PROCESSING))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private BigDecimal sumByStatus(List<Transaction> txs, TransactionStatus status) {
        return txs.stream()
                .filter(t -> status.equals(t.getStatus()))
                .map(t -> t.getAmount() != null ? t.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private TransactionStatus parseStatus(String status) {
        if (status == null || status.isEmpty()) return null;
        try { return TransactionStatus.valueOf(status.toUpperCase()); }
        catch (IllegalArgumentException e) { return null; }
    }

    private Designation parseDesignation(String designation) {
        if (designation == null || designation.isEmpty()) return null;
        try { return Designation.valueOf(designation.toUpperCase()); }
        catch (IllegalArgumentException e) { return null; }
    }

    private String buildDateLabel(String fromDate, String toDate) {
        if (fromDate != null && !fromDate.isEmpty() && toDate != null && !toDate.isEmpty())
            return fromDate + " to " + toDate;
        if (fromDate != null && !fromDate.isEmpty()) return "From " + fromDate;
        if (toDate   != null && !toDate.isEmpty())   return "Up to " + toDate;
        return "Year to date";
    }

    // =====================================================================
    // Cell style helpers
    // =====================================================================

    private CellStyle createHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.TEAL.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createSubHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    private CellStyle createTitleStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        return style;
    }

    private CellStyle createBoldStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private CellStyle createCurrencyStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        CreationHelper ch = wb.getCreationHelper();
        style.setDataFormat(ch.createDataFormat().getFormat("\u20a6#,##0.00"));
        return style;
    }

    // =====================================================================
    // Cell creation helpers
    // =====================================================================

    private void strCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value != null ? value : "");
        if (style != null) cell.setCellStyle(style);
    }

    private void numCell(Row row, int col, double value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        if (style != null) cell.setCellStyle(style);
    }

    private void numCell(Row row, int col, long value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        if (style != null) cell.setCellStyle(style);
    }

    // =====================================================================
    // Sheet builders
    // =====================================================================

    private void buildSummarySheet(Workbook wb, FinancialReportSummaryDto summary, String estateId) {
        Sheet sheet = wb.createSheet("Summary");
        CellStyle titleStyle    = createTitleStyle(wb);
        CellStyle boldStyle     = createBoldStyle(wb);
        CellStyle subHdrStyle   = createSubHeaderStyle(wb);
        CellStyle currencyStyle = createCurrencyStyle(wb);
        int rowNum = 0;

        // ---- Title ----
        Row titleRow = sheet.createRow(rowNum++);
        strCell(titleRow, 0, "COMPREHENSIVE FINANCIAL REPORT", titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
        rowNum++; // blank

        // ---- Metadata ----
        Row r1 = sheet.createRow(rowNum++);
        strCell(r1, 0, "Date Range:", boldStyle);
        strCell(r1, 1, summary.getDateRangeLabel(), null);
        Row r2 = sheet.createRow(rowNum++);
        strCell(r2, 0, "Estate ID:", boldStyle);
        strCell(r2, 1, estateId != null ? estateId : "All", null);
        Row r3 = sheet.createRow(rowNum++);
        strCell(r3, 0, "Generated At:", boldStyle);
        strCell(r3, 1, summary.getGeneratedAt(), null);
        rowNum++; // blank

        // ---- Key Metrics ----
        strCell(sheet.createRow(rowNum++), 0, "KEY METRICS", titleStyle);
        Row mh = sheet.createRow(rowNum++);
        strCell(mh, 0, "Metric", subHdrStyle);
        strCell(mh, 1, "Value", subHdrStyle);

        Object[][] metrics = {
            {"Total Transactions",      summary.getTotalTransactionCount()},
            {"Completed Transactions",  summary.getCompletedTransactionCount()},
            {"Failed Transactions",     summary.getFailedTransactionCount()},
//            {"Processing Transactions", summary.getProcessingTransactionCount()},
//            {"Rejected Transactions",   summary.getRejectedTransactionCount()},
        };
        for (Object[] m : metrics) {
            Row row = sheet.createRow(rowNum++);
            strCell(row, 0, (String) m[0], null);
            numCell(row, 1, (Long) m[1], null);
        }
        Row revRow = sheet.createRow(rowNum++);
        strCell(revRow, 0, "Completed Revenue", null);
        numCell(revRow, 1, summary.getTotalCompletedRevenue().doubleValue(), currencyStyle);
        Row failRow = sheet.createRow(rowNum++);
        strCell(failRow, 0, "Total Failed Amount", null);
        numCell(failRow, 1, summary.getTotalFailedAmount().doubleValue(), currencyStyle);
//        Row procRow = sheet.createRow(rowNum++);
//        strCell(procRow, 0, "Total Processing Amount", null);
//        numCell(procRow, 1, summary.getTotalProcessingAmount().doubleValue(), currencyStyle);
        Row avgRow = sheet.createRow(rowNum++);
        strCell(avgRow, 0, "Average Transaction Amount", null);
        numCell(avgRow, 1, summary.getAverageTransactionAmount().doubleValue(), currencyStyle);
        rowNum++; // blank

        // ---- Product Breakdown Preview ----
        strCell(sheet.createRow(rowNum++), 0, "PRODUCT BREAKDOWN PREVIEW", titleStyle);
        Row pbh = sheet.createRow(rowNum++);
        for (int i = 0; i < 4; i++) strCell(pbh, i,
                new String[]{"Product", "Count", "Completed Revenue", "Failed Amount"}[i], subHdrStyle);

        for (FinancialReportSummaryDto.ProductBreakdownItem item : summary.getProductBreakdown()) {
            Row row = sheet.createRow(rowNum++);
            strCell(row, 0, item.getProductName(), null);
            numCell(row, 1, item.getTransactionCount(), null);
            numCell(row, 2, item.getCompletedRevenue().doubleValue(), currencyStyle);
            numCell(row, 3, item.getFailedAmount().doubleValue(), currencyStyle);
        }
        rowNum++; // blank

        // ---- Designation Breakdown Preview ----
        strCell(sheet.createRow(rowNum++), 0, "DESIGNATION BREAKDOWN PREVIEW", titleStyle);
        Row dbh = sheet.createRow(rowNum++);
        for (int i = 0; i < 4; i++) strCell(dbh, i,
                new String[]{"Designation", "Count", "Completed Revenue", "Failed Amount"}[i], subHdrStyle);

        for (FinancialReportSummaryDto.DesignationBreakdownItem item : summary.getDesignationBreakdown()) {
            Row row = sheet.createRow(rowNum++);
            strCell(row, 0, item.getDesignation(), null);
            numCell(row, 1, item.getTransactionCount(), null);
            numCell(row, 2, item.getCompletedRevenue().doubleValue(), currencyStyle);
            numCell(row, 3, item.getFailedAmount().doubleValue(), currencyStyle);
        }

        for (int i = 0; i < 6; i++) sheet.autoSizeColumn(i);
    }

    private void buildTransactionsSheet(Workbook wb, List<Transaction> transactions) {
        Sheet sheet = wb.createSheet("Transactions");
        CellStyle headerStyle   = createHeaderStyle(wb);
        CellStyle currencyStyle = createCurrencyStyle(wb);

        String[] headers = {
            "S/N", "Reference", "Transaction ID", "Amount", "Fee",
            "Product", "User ID", "Designation", "Status",
            "Payment Mode", "Channel", "Created At", "Paid At",
            "Subscribe From", "Subscribe To", "Estate ID", "Description"
        };
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) strCell(headerRow, i, headers[i], headerStyle);

        int rowNum = 1;
        for (Transaction t : transactions) {
            Row row = sheet.createRow(rowNum);
            numCell(row, 0, rowNum, null);
            strCell(row, 1, t.getReference(), null);
            strCell(row, 2, t.getTransactionId(), null);
            numCell(row, 3, t.getAmount() != null ? t.getAmount().doubleValue() : 0, currencyStyle);
            numCell(row, 4, t.getFee()    != null ? t.getFee().doubleValue()    : 0, currencyStyle);
            strCell(row, 5, t.getProductName(), null);
            strCell(row, 6, t.getUserId(), null);
            strCell(row, 7, t.getDesignation() != null ? t.getDesignation().name() : "", null);
            strCell(row, 8, t.getStatus()      != null ? t.getStatus().name()      : "", null);
            strCell(row, 9, t.getPaymentMode() != null ? t.getPaymentMode().name() : "", null);
            strCell(row, 10, t.getChannel(), null);
            strCell(row, 11, t.getCreatedAt()    != null ? t.getCreatedAt().format(DT_FORMATTER)    : "", null);
            strCell(row, 12, t.getPaidAt()       != null ? t.getPaidAt().format(DT_FORMATTER)       : "", null);
            strCell(row, 13, t.getSubscribeFrom() != null ? t.getSubscribeFrom().format(DT_FORMATTER) : "", null);
            strCell(row, 14, t.getSubscribeTo()  != null ? t.getSubscribeTo().format(DT_FORMATTER)  : "", null);
            strCell(row, 15, t.getEstateId(), null);
            strCell(row, 16, t.getDescription(), null);
            rowNum++;
        }
        for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
    }

    private void buildProductBreakdownSheet(Workbook wb,
            List<FinancialReportSummaryDto.ProductBreakdownItem> items) {
        Sheet sheet = wb.createSheet("Product Breakdown");
        CellStyle headerStyle   = createHeaderStyle(wb);
        CellStyle currencyStyle = createCurrencyStyle(wb);

        String[] headers = {"Product Name", "Transaction Count", "Completed Revenue", "Failed Amount", "Processing Amount"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) strCell(headerRow, i, headers[i], headerStyle);

        int rowNum = 1;
        for (FinancialReportSummaryDto.ProductBreakdownItem item : items) {
            Row row = sheet.createRow(rowNum++);
            strCell(row, 0, item.getProductName(), null);
            numCell(row, 1, item.getTransactionCount(), null);
            numCell(row, 2, item.getCompletedRevenue().doubleValue(), currencyStyle);
            numCell(row, 3, item.getFailedAmount().doubleValue(), currencyStyle);
            numCell(row, 4, item.getProcessingAmount().doubleValue(), currencyStyle);
        }
        for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
    }

    private void buildDesignationBreakdownSheet(Workbook wb,
            List<FinancialReportSummaryDto.DesignationBreakdownItem> items) {
        Sheet sheet = wb.createSheet("Designation Breakdown");
        CellStyle headerStyle   = createHeaderStyle(wb);
        CellStyle currencyStyle = createCurrencyStyle(wb);

        String[] headers = {"Designation", "Transaction Count", "Completed Revenue", "Failed Amount"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) strCell(headerRow, i, headers[i], headerStyle);

        int rowNum = 1;
        for (FinancialReportSummaryDto.DesignationBreakdownItem item : items) {
            Row row = sheet.createRow(rowNum++);
            strCell(row, 0, item.getDesignation(), null);
            numCell(row, 1, item.getTransactionCount(), null);
            numCell(row, 2, item.getCompletedRevenue().doubleValue(), currencyStyle);
            numCell(row, 3, item.getFailedAmount().doubleValue(), currencyStyle);
        }
        for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
    }

    private void buildMonthlyTrendSheet(Workbook wb,
            List<FinancialReportSummaryDto.MonthlyTrendItem> items) {
        Sheet sheet = wb.createSheet("Monthly Trend");
        CellStyle headerStyle   = createHeaderStyle(wb);
        CellStyle currencyStyle = createCurrencyStyle(wb);

        String[] headers = {
            "Month", "Month Label", "Transaction Count",
            "Completed Revenue", "Failed Amount", "Processing Amount"
        };
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) strCell(headerRow, i, headers[i], headerStyle);

        int rowNum = 1;
        for (FinancialReportSummaryDto.MonthlyTrendItem item : items) {
            Row row = sheet.createRow(rowNum++);
            strCell(row, 0, item.getMonth(), null);
            strCell(row, 1, item.getMonthLabel(), null);
            numCell(row, 2, item.getTransactionCount(), null);
            numCell(row, 3, item.getCompletedRevenue().doubleValue(), currencyStyle);
            numCell(row, 4, item.getFailedAmount().doubleValue(), currencyStyle);
            numCell(row, 5, item.getProcessingAmount().doubleValue(), currencyStyle);
        }
        for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
    }
}
