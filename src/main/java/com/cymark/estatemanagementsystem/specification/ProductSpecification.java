package com.cymark.estatemanagementsystem.specification;

import com.cymark.estatemanagementsystem.model.entity.Estate;
import com.cymark.estatemanagementsystem.model.entity.Product;
import org.springframework.data.jpa.domain.Specification;
import java.time.Instant;

public final class ProductSpecification {

    public static Specification<Product> onSpecificInstant(Instant specificInstant) {
        return (root, query, builder) -> {
            if (specificInstant != null) {
                Instant startOfDay = specificInstant.truncatedTo(java.time.temporal.ChronoUnit.DAYS);
                Instant endOfDay = startOfDay.plus(java.time.Duration.ofDays(1));
                return builder.between(root.get("dateCreated"), startOfDay, endOfDay);
            }
            return null;
        };
    }

    public static Specification<Product> betweenInstants(Instant startDate, Instant endDate) {
        return (root, query, builder) -> {
            if (startDate != null && endDate != null) {
                return builder.between(root.get("dateCreated"), startDate, endDate);
            }
            return null;
        };
    }

    public static Specification<Product> publishedEquals(Boolean isPublished) {
        return (root, query, builder) ->
                isPublished != null ? builder.equal(root.get("publishStatus"), isPublished) : null;
    }

    public static Specification<Product> nameEqual(String name) {
        return (root, query, builder) ->
                name != null ? builder.equal(root.get("name"), name) : null;
    }

    public static Specification<Product> designationEqual(String designation) {
        return (root, query, builder) ->
                designation != null ? builder.equal(root.get("designation"), designation) : null;
    }

}


