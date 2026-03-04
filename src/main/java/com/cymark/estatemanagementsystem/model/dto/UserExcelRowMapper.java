package com.cymark.estatemanagementsystem.model.dto;

import java.math.BigDecimal;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import org.springframework.jdbc.core.RowMapper;
//
//
//public class UserExcelRowMapper implements RowMapper<UserExcelRow> {
//    @Override
//    public UserExcelRow mapRow(ResultSet rs, int rowNum) throws SQLException {
//        BigDecimal amountPaid = rs.getBigDecimal("amount_paid_this_year");
//        BigDecimal outstandingDebt = rs.getBigDecimal("outstanding_debt");
//
//        return UserExcelRow.builder()
//                .firstName(rs.getString("first_name"))
//                .lastName(rs.getString("last_name"))
//                .fullName(rs.getString("full_name"))
//                .email(rs.getString("email_address"))
//                .phoneNumber(rs.getString("phone_number"))
//                .designation(rs.getString("designation"))
//                .fullAddress(rs.getString("full_address"))
//                .country(rs.getString("country"))
//                .nationality(rs.getString("nationality"))
//                .lastPaid(rs.getTimestamp("last_paid_date") != null ?
//                        rs.getTimestamp("last_paid_date").toLocalDateTime() : null)
//                .amountPaidThisYear(amountPaid)
//                .outstandingDebt(outstandingDebt)
//                .landlordFullName(rs.getString("landlord_full_name"))
//                .tenantFullNames(rs.getString("tenant_names"))
//                .occupantFullNames(rs.getString("occupant_names"))
//                .enabled(rs.getBoolean("is_enabled"))
//                .estateName(rs.getString("estate_name"))
//                .userId(rs.getString("user_id"))
//                .build();
//    }
//}
//

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.math.BigDecimal;

public class UserExcelRowMapper implements RowMapper<UserExcelRow> {
    @Override
    public UserExcelRow mapRow(ResultSet rs, int rowNum) throws SQLException {
        return UserExcelRow.builder()
                .userId(rs.getString("user_id"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .fullName(rs.getString("full_name"))
                .email(rs.getString("email_address"))
                .phoneNumber(rs.getString("phone_number"))
                .designation(rs.getString("designation"))
                .fullAddress(rs.getString("full_address"))
                .country(rs.getString("country"))
                .nationality(rs.getString("nationality"))
                .lastPaid(rs.getTimestamp("last_paid_date") != null ?
                        rs.getTimestamp("last_paid_date").toLocalDateTime() : null)
                .amountPaidThisYear(rs.getBigDecimal("amount_paid_this_year"))
                .outstandingDebt(rs.getBigDecimal("outstanding_debt"))
                .landlordFullName(rs.getString("landlord_full_name"))
                .tenantFullNames(rs.getString("tenant_names"))
                .occupantFullNames(rs.getString("occupant_names"))
                .enabled(rs.getBoolean("is_enabled"))
                .estateName(rs.getString("estate_name"))
                .build();
    }
}