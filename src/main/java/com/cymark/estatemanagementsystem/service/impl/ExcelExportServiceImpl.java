package com.cymark.estatemanagementsystem.service.impl;

import com.cymark.estatemanagementsystem.model.dto.UserExcelRow;
import com.cymark.estatemanagementsystem.repository.UserExcelRepository;
import com.cymark.estatemanagementsystem.service.ExcelExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelExportServiceImpl implements ExcelExportService {

    private final UserExcelRepository userExcelRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void exportUsersToExcel(
            HttpServletResponse response,
            String firstName,
            String lastName,
            String email,
            Long roleId,
            Boolean isActive,
            String designation,
            Sort.Direction sortDirection,
            String sortProperty) throws IOException {

        log.info("Exporting users to Excel with filters - firstName: {}, lastName: {}, email: {}, roleId: {}, isActive: {}, designation: {}",
                firstName, lastName, email, roleId, isActive, designation);

        String sortField = mapSortProperty(sortProperty != null ? sortProperty : "firstName");
        String direction = sortDirection != null ? sortDirection.name() : "ASC";

        List<UserExcelRow> users = userExcelRepository.findUsersForExcelExportByEstateId(
                firstName, lastName, email, roleId, isActive, designation, sortField, direction);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Users");

            // Create header row
            createHeaderRow(sheet, workbook);

            // Create data rows
            int rowNum = 1;
            for (UserExcelRow user : users) {
                Row row = sheet.createRow(rowNum++);
                createDataRow(row, user, workbook);
            }

            // Auto-size columns
            for (int i = 0; i < 15; i++) {
                sheet.autoSizeColumn(i);
            }

            // Set response headers
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=users_export_" + 
                System.currentTimeMillis() + ".xlsx");

            // Write to response output stream
            workbook.write(response.getOutputStream());
        }
    }

    private String mapSortProperty(String sortProperty) {
        return switch (sortProperty) {
            case "firstName" -> "u.first_name";
            case "lastName" -> "u.last_name";
            case "email" -> "u.email_address";
            case "designation" -> "u.designation";
            default -> "u.first_name";
        };
    }

    private void createHeaderRow(Sheet sheet, Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Row headerRow = sheet.createRow(0);
        String[] columns = {
            "Full Name", "First Name", "Last Name", "Email", "Phone Number", 
            "Designation", "Full Address", "Country", "Nationality", "Last Paid Date",
            "Amount Paid This Year", "Outstanding Debt", "Landlord Full Name",
            "Tenants (for Landlords)", "Occupants (for Tenants)", "Status", "Estate", "User ID"
        };

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void createDataRow(Row row, UserExcelRow user, Workbook workbook) {
        CellStyle dateStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-mm-dd hh:mm"));

        CellStyle currencyStyle = workbook.createCellStyle();
        currencyStyle.setDataFormat(createHelper.createDataFormat().getFormat("$#,##0.00"));

        row.createCell(0).setCellValue(user.getFullName() != null ? user.getFullName() : "");
        row.createCell(1).setCellValue(user.getFirstName() != null ? user.getFirstName() : "");
        row.createCell(2).setCellValue(user.getLastName() != null ? user.getLastName() : "");
        row.createCell(3).setCellValue(user.getEmail() != null ? user.getEmail() : "");
        row.createCell(4).setCellValue(user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
        row.createCell(5).setCellValue(user.getDesignation() != null ? user.getDesignation() : "");
        row.createCell(6).setCellValue(user.getFullAddress() != null ? user.getFullAddress() : "");
        row.createCell(7).setCellValue(user.getCountry() != null ? user.getCountry() : "");
        row.createCell(8).setCellValue(user.getNationality() != null ? user.getNationality() : "");
        
        // Date cell
        Cell lastPaidCell = row.createCell(9);
        if (user.getLastPaid() != null) {
            lastPaidCell.setCellValue(user.getLastPaid());
            lastPaidCell.setCellStyle(dateStyle);
        } else {
            lastPaidCell.setCellValue("");
        }

        // Currency cells
        Cell amountPaidCell = row.createCell(10);
        if (user.getAmountPaidThisYear() != null) {
            amountPaidCell.setCellValue(user.getAmountPaidThisYear().doubleValue());
            amountPaidCell.setCellStyle(currencyStyle);
        } else {
            amountPaidCell.setCellValue(BigDecimal.ZERO.doubleValue());
            amountPaidCell.setCellStyle(currencyStyle);
        }

        Cell outstandingCell = row.createCell(11);
        if (user.getOutstandingDebt() != null) {
            outstandingCell.setCellValue(user.getOutstandingDebt().doubleValue());
            outstandingCell.setCellStyle(currencyStyle);
        } else {
            outstandingCell.setCellValue(BigDecimal.ZERO.doubleValue());
            outstandingCell.setCellStyle(currencyStyle);
        }

        row.createCell(12).setCellValue(user.getLandlordFullName() != null ? user.getLandlordFullName() : "");
        row.createCell(13).setCellValue(user.getTenantFullNames() != null ? user.getTenantFullNames() : "");
        row.createCell(14).setCellValue(user.getOccupantFullNames() != null ? user.getOccupantFullNames() : "");
        row.createCell(15).setCellValue(user.isEnabled() ? "Active" : "Inactive");
        row.createCell(16).setCellValue(user.getEstateName() != null ? user.getEstateName() : "");
        row.createCell(17).setCellValue(user.getUserId() != null ? user.getUserId() : "");
    }
}