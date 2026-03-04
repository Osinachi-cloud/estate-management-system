package com.cymark.estatemanagementsystem.service;

import com.cymark.estatemanagementsystem.model.dto.UserExcelRow;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ExcelExportService {
    void exportUsersToExcel(
        HttpServletResponse response,
        String firstName,
        String lastName,
        String email,
        Long roleId,
        Boolean isActive,
        String designation,
        Sort.Direction sortDirection,
        String sortProperty
    ) throws IOException;
}