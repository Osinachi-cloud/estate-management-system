package com.cymark.estatemanagementsystem.repository;

import com.cymark.estatemanagementsystem.model.dto.UserExcelRow;
import com.cymark.estatemanagementsystem.model.dto.UserExcelRowMapper;import com.cymark.estatemanagementsystem.model.entity.Estate;import com.cymark.estatemanagementsystem.model.entity.UserEntity;import com.cymark.estatemanagementsystem.model.enums.Designation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserExcelRepository {

    private final UserRepository userRepository;

    private final JdbcTemplate jdbcTemplate;

    public List<UserExcelRow> findUsersForExcelExport(
            String firstName, String lastName, String email,
            Long roleId, Boolean isActive, String designation,
            String sortField, String sortDirection) {

//        String loggedInUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
//        UserEntity loggedInUser = userRepository.findUserEntityByEmail(loggedInUserEmail);
//        String estateId = loggedInUser.getEstateId();


        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("WITH user_payments AS (");
        sql.append("    SELECT ");
        sql.append("        u.user_id, ");
        sql.append("        u.first_name, ");
        sql.append("        u.last_name, ");
        sql.append("        CONCAT(u.first_name, ' ', u.last_name) as full_name, ");
        sql.append("        u.email_address, ");
        sql.append("        u.phone_number, ");
        sql.append("        u.designation, ");
        sql.append("        u.country, ");
        sql.append("        u.nationality, ");
        sql.append("        u.is_enabled, ");
        sql.append("        u.land_lord_id, ");
        sql.append("        u.tenant_id, ");
        sql.append("        e.name as estate_name, ");
        sql.append("        MAX(CASE WHEN o.status = 'COMPLETED' THEN o.last_updated END) as last_paid_date, ");
        sql.append("        COALESCE(SUM(CASE ");
        sql.append("            WHEN o.status = 'COMPLETED' ");
        sql.append("            AND EXTRACT(YEAR FROM o.last_updated) = EXTRACT(YEAR FROM CURRENT_DATE) ");
        sql.append("            THEN o.amount ELSE 0 END), 0) as amount_paid_this_year, ");
        sql.append("        (SELECT STRING_AGG(lu.first_name || ' ' || lu.last_name, ', ') ");
        sql.append("         FROM user_entity lu ");
        sql.append("         WHERE lu.land_lord_id = u.user_id) as tenant_names, ");
        sql.append("        (SELECT STRING_AGG(ou.first_name || ' ' || ou.last_name, ', ') ");
        sql.append("         FROM user_entity ou ");
        sql.append("         WHERE ou.tenant_id = u.user_id) as occupant_names, ");
        sql.append("        (SELECT l.first_name || ' ' || l.last_name ");
        sql.append("         FROM user_entity l ");
        sql.append("         WHERE l.user_id = u.land_lord_id) as landlord_full_name, ");
        sql.append("        (SELECT CONCAT_WS(', ', ");
        sql.append("                a.street, ");
        sql.append("                a.house_number, ");
        sql.append("                a.apartment_number, ");
        sql.append("                a.postal_code, ");
        sql.append("                a.full_address) ");
        sql.append("         FROM address a ");
        sql.append("         JOIN user_address ua ON a.id = ua.address_id ");
        sql.append("         WHERE ua.user_id = u.user_id ");
        sql.append("         LIMIT 1) as full_address ");
        sql.append("    FROM user_entity u ");
        sql.append("    LEFT JOIN estate e ON u.estate_id = e.estate_id ");
        sql.append("    LEFT JOIN orders o ON u.email_address = o.email_address ");
        sql.append("    LEFT JOIN user_address ua ON u.user_id = ua.user_id ");
        sql.append("    LEFT JOIN address a ON ua.address_id = a.id ");
        sql.append("    WHERE 1=1 ");

        // Add filters
        if (firstName != null && !firstName.isEmpty()) {
            sql.append(" AND u.first_name ILIKE ? ");
            params.add("%" + firstName + "%");
        }
        if (lastName != null && !lastName.isEmpty()) {
            sql.append(" AND u.last_name ILIKE ? ");
            params.add("%" + lastName + "%");
        }
        if (email != null && !email.isEmpty()) {
            sql.append(" AND u.email_address ILIKE ? ");
            params.add("%" + email + "%");
        }
        if (roleId != null) {
            sql.append(" AND u.role_id = ? ");
            params.add(roleId);
        }
        if (isActive != null) {
            sql.append(" AND u.is_enabled = ? ");
            params.add(isActive);
        }
        if (designation != null && !designation.isEmpty()) {
            sql.append(" AND u.designation = ? ");
            params.add(designation);
        }

        sql.append("    GROUP BY ");
        sql.append("        u.user_id, u.first_name, u.last_name, u.email_address, ");
        sql.append("        u.phone_number, u.designation, u.country, u.nationality, ");
        sql.append("        u.is_enabled, u.land_lord_id, u.tenant_id, e.name ");
        sql.append(") ");
        sql.append("SELECT ");
        sql.append("    up.user_id, ");
        sql.append("    up.first_name, ");
        sql.append("    up.last_name, ");
        sql.append("    up.full_name, ");
        sql.append("    up.email_address, ");
        sql.append("    up.phone_number, ");
        sql.append("    up.designation, ");
        sql.append("    up.country, ");
        sql.append("    up.nationality, ");
        sql.append("    up.is_enabled, ");
        sql.append("    up.land_lord_id, ");
        sql.append("    up.tenant_id, ");
        sql.append("    up.estate_name, ");
        sql.append("    up.last_paid_date, ");
        sql.append("    up.amount_paid_this_year, ");
        sql.append("    up.tenant_names, ");
        sql.append("    up.occupant_names, ");
        sql.append("    up.landlord_full_name, ");
        sql.append("    up.full_address, ");
        sql.append("    CASE ");
        sql.append("        WHEN up.designation = 'TENANT' THEN ");
        sql.append("            (up.amount_paid_this_year * 12 - up.amount_paid_this_year) ");
        sql.append("        ELSE 0 ");
        sql.append("    END as outstanding_debt ");
        sql.append("FROM user_payments up ");

        // Handle sorting - map the sortField to actual column names in the CTE
        String orderByColumn = switch (sortField) {
            case "u.first_name", "firstName" -> "up.first_name";
            case "u.last_name", "lastName" -> "up.last_name";
            case "u.email_address", "email" -> "up.email_address";
            case "u.designation", "designation" -> "up.designation";
            default -> "up.first_name";
        };

        sql.append(" ORDER BY ").append(orderByColumn).append(" ").append(sortDirection);

        return jdbcTemplate.query(sql.toString(), params.toArray(), new UserExcelRowMapper());
    }


    public List<UserExcelRow> findUsersForExcelExportByEstateId(
            String firstName, String lastName, String email,
            Long roleId, Boolean isActive, String designation,
            String sortField, String sortDirection) {

        String loggedInUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity loggedInUser = userRepository.findUserEntityByEmail(loggedInUserEmail);
        String estateId = loggedInUser.getEstateId();

        // Debug logging
        log.info("Logged in user: {}, estateId: {}", loggedInUserEmail, estateId);

        // Debug: Check if estate exists
        try {
            String checkEstateSql = "SELECT name FROM estate WHERE estate_id = ?";
            String estateName = jdbcTemplate.queryForObject(checkEstateSql, new Object[]{estateId}, String.class);
            log.info("Estate found with name: {}", estateName);
        } catch (Exception e) {
            log.warn("No estate found with ID: {}", estateId);
        }

        // Debug: Check address data for users
        String checkAddressSql = "SELECT COUNT(*) FROM address a JOIN user_address ua ON a.id = ua.address_id";
        Integer addressCount = jdbcTemplate.queryForObject(checkAddressSql, Integer.class);
        log.info("Total address associations: {}", addressCount);

        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("WITH user_payments AS (");
        sql.append("    SELECT ");
        sql.append("        u.user_id, ");
        sql.append("        u.first_name, ");
        sql.append("        u.last_name, ");
        sql.append("        CONCAT(u.first_name, ' ', u.last_name) as full_name, ");
        sql.append("        u.email_address, ");
        sql.append("        u.phone_number, ");
        sql.append("        u.designation, ");
        sql.append("        u.country, ");
        sql.append("        u.nationality, ");
        sql.append("        u.is_enabled, ");
        sql.append("        u.land_lord_id, ");
        sql.append("        u.tenant_id, ");
        sql.append("        COALESCE(e.name, 'No Estate') as estate_name, "); // Handle NULL estate names
        sql.append("        MAX(CASE WHEN o.status = 'COMPLETED' THEN o.last_updated END) as last_paid_date, ");
        sql.append("        COALESCE(SUM(CASE ");
        sql.append("            WHEN o.status = 'COMPLETED' ");
        sql.append("            AND EXTRACT(YEAR FROM o.last_updated) = EXTRACT(YEAR FROM CURRENT_DATE) ");
        sql.append("            THEN o.amount ELSE 0 END), 0) as amount_paid_this_year, ");
        sql.append("        (SELECT STRING_AGG(lu.first_name || ' ' || lu.last_name, ', ') ");
        sql.append("         FROM user_entity lu ");
        sql.append("         WHERE lu.land_lord_id = u.user_id) as tenant_names, ");
        sql.append("        (SELECT STRING_AGG(ou.first_name || ' ' || ou.last_name, ', ') ");
        sql.append("         FROM user_entity ou ");
        sql.append("         WHERE ou.tenant_id = u.user_id) as occupant_names, ");
        sql.append("        (SELECT l.first_name || ' ' || l.last_name ");
        sql.append("         FROM user_entity l ");
        sql.append("         WHERE l.user_id = u.land_lord_id) as landlord_full_name, ");
        sql.append("        (SELECT ");
        sql.append("            CONCAT_WS(', ', ");
        sql.append("                COALESCE(a.street, ''), ");
        sql.append("                COALESCE(a.house_number, ''), ");
        sql.append("                COALESCE(a.apartment_number, ''), ");
        sql.append("                COALESCE(a.postal_code, ''), ");
        sql.append("                COALESCE(a.full_address, '')");
        sql.append("            ) ");
        sql.append("         FROM address a ");
        sql.append("         JOIN user_address ua ON a.id = ua.address_id ");
        sql.append("         WHERE ua.user_id = u.user_id ");
        sql.append("         LIMIT 1) as full_address ");
        sql.append("    FROM user_entity u ");
        sql.append("    LEFT JOIN estate e ON u.estate_id = e.estate_id ");
        sql.append("    LEFT JOIN orders o ON u.email_address = o.email_address ");
        sql.append("    LEFT JOIN user_address ua ON u.user_id = ua.user_id ");
        sql.append("    LEFT JOIN address a ON ua.address_id = a.id ");
        sql.append("    WHERE 1=1 ");
        sql.append("    AND u.estate_id = ? "); // Filter by estate
        params.add(estateId);

        // Add optional filters
        if (firstName != null && !firstName.isEmpty()) {
            sql.append(" AND u.first_name ILIKE ? ");
            params.add("%" + firstName + "%");
        }
        if (lastName != null && !lastName.isEmpty()) {
            sql.append(" AND u.last_name ILIKE ? ");
            params.add("%" + lastName + "%");
        }
        if (email != null && !email.isEmpty()) {
            sql.append(" AND u.email_address ILIKE ? ");
            params.add("%" + email + "%");
        }
        if (roleId != null) {
            sql.append(" AND u.role_id = ? ");
            params.add(roleId);
        }
        if (isActive != null) {
            sql.append(" AND u.is_enabled = ? ");
            params.add(isActive);
        }
        if (designation != null && !designation.isEmpty()) {
            sql.append(" AND u.designation = ? ");
            params.add(designation);
        }

        sql.append("    GROUP BY ");
        sql.append("        u.user_id, u.first_name, u.last_name, u.email_address, ");
        sql.append("        u.phone_number, u.designation, u.country, u.nationality, ");
        sql.append("        u.is_enabled, u.land_lord_id, u.tenant_id, e.name ");
        sql.append(") ");
        sql.append("SELECT ");
        sql.append("    up.user_id, ");
        sql.append("    up.first_name, ");
        sql.append("    up.last_name, ");
        sql.append("    up.full_name, ");
        sql.append("    up.email_address, ");
        sql.append("    up.phone_number, ");
        sql.append("    up.designation, ");
        sql.append("    up.country, ");
        sql.append("    up.nationality, ");
        sql.append("    up.is_enabled, ");
        sql.append("    up.land_lord_id, ");
        sql.append("    up.tenant_id, ");
        sql.append("    up.estate_name, ");
        sql.append("    up.last_paid_date, ");
        sql.append("    up.amount_paid_this_year, ");
        sql.append("    up.tenant_names, ");
        sql.append("    up.occupant_names, ");
        sql.append("    up.landlord_full_name, ");
        sql.append("    CASE WHEN up.full_address = '' THEN 'No Address' ELSE up.full_address END as full_address, "); // Handle empty address
        sql.append("    CASE ");
        sql.append("        WHEN up.designation = 'TENANT' THEN ");
        sql.append("            (up.amount_paid_this_year * 12 - up.amount_paid_this_year) ");
        sql.append("        ELSE 0 ");
        sql.append("    END as outstanding_debt ");
        sql.append("FROM user_payments up ");


        // Handle sorting
        String orderByColumn = switch (sortField) {
            case "u.first_name", "firstName" -> "up.first_name";
            case "u.last_name", "lastName" -> "up.last_name";
            case "u.email_address", "email" -> "up.email_address";
            case "u.designation", "designation" -> "up.designation";
            default -> "up.first_name";
        };

        sql.append(" ORDER BY ").append(orderByColumn).append(" ").append(sortDirection);

        // Log the final SQL for debugging
        log.debug("Final SQL: {}", sql.toString());
        log.debug("Params: {}", params);

        List<UserExcelRow> results = jdbcTemplate.query(sql.toString(), params.toArray(), new UserExcelRowMapper());

        // Debug: Check first few results
        if (!results.isEmpty()) {
            UserExcelRow first = results.get(0);
            log.info("First result - User: {} {}, Estate: {}, Address: {}",
                    first.getFirstName(), first.getLastName(), first.getEstateName(), first.getFullAddress());
        }

        return results;
    }
}



