package com.cymark.estatemanagementsystem.specification;


import com.cymark.estatemanagementsystem.model.entity.Transaction;
import com.cymark.estatemanagementsystem.model.enums.TransactionStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionSpecifications {

    public static Specification<Transaction> withFilters(
            String reference,
            TransactionStatus status,
            String productName,
            String userId,
            LocalDateTime fromDate,
            LocalDateTime toDate) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (reference != null && !reference.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("reference")),
                        "%" + reference.toLowerCase() + "%"
                ));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (productName != null && !productName.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("productName")),
                        "%" + productName.toLowerCase() + "%"
                ));
            }

            if (userId != null && !userId.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("userId"), userId));
            }

            if (fromDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), fromDate));
            }

            if (toDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), toDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }





    public static Specification<Transaction> withStatus(TransactionStatus status) {
        return (root, query, cb) -> status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    public static Specification<Transaction> withCreatedAtBetween(LocalDate fromDate, LocalDate toDate) {
        return (root, query, cb) -> {
            if (fromDate == null && toDate == null) {
                return cb.conjunction();
            }
            if (fromDate != null && toDate != null) {
                return cb.between(root.get("createdAt"), fromDate.atStartOfDay(), toDate.atTime(23, 59, 59));
            }
            if (fromDate != null) {
                return cb.greaterThanOrEqualTo(root.get("createdAt"), fromDate.atStartOfDay());
            }
            return cb.lessThanOrEqualTo(root.get("createdAt"), toDate.atTime(23, 59, 59));
        };
    }

    public static Specification<Transaction> withCreatedAtNotNull() {
        return (root, query, cb) -> cb.isNotNull(root.get("createdAt"));
    }
}