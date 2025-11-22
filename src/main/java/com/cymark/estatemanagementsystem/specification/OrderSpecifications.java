package com.cymark.estatemanagementsystem.specification;

import com.cymark.estatemanagementsystem.model.entity.Order;
import com.cymark.estatemanagementsystem.model.enums.OrderStatus;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class OrderSpecifications {

    public static Specification<Order> withEmail(String email) {
        return (root, query, cb) ->
                email == null || email.isEmpty() ? cb.conjunction() : cb.equal(root.get("emailAddress"), email);
    }

    public static Specification<Order> withStatus(OrderStatus status) {
        return (root, query, cb) -> status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    public static Specification<Order> withSubscribeForBetween(LocalDate fromDate, LocalDate toDate) {
        return (root, query, cb) -> {
            if (fromDate == null && toDate == null) {
                return cb.conjunction();
            }
            if (fromDate != null && toDate != null) {
                return cb.between(root.get("subscribeFor"), fromDate.atStartOfDay(), toDate.atTime(23, 59, 59));
            }
            if (fromDate != null) {
                return cb.greaterThanOrEqualTo(root.get("subscribeFor"), fromDate.atStartOfDay());
            }
            return cb.lessThanOrEqualTo(root.get("subscribeFor"), toDate.atTime(23, 59, 59));
        };
    }

    public static Specification<Order> withSubscribeForNotNull() {
        return (root, query, cb) -> cb.isNotNull(root.get("subscribeFor"));
    }
}