package com.cymark.estatemanagementsystem.specification;

import com.cymark.estatemanagementsystem.model.entity.Estate;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public final class EstateSpecification {

    public static Specification<Estate> onSpecificInstant(Instant specificInstant) {
        return (root, query, builder) -> {
            if (specificInstant != null) {
                Instant startOfDay = specificInstant.truncatedTo(java.time.temporal.ChronoUnit.DAYS);
                Instant endOfDay = startOfDay.plus(java.time.Duration.ofDays(1));
                return builder.between(root.get("dateCreated"), startOfDay, endOfDay);
            }
            return null;
        };
    }

    public static Specification<Estate> betweenInstants(Instant startDate, Instant endDate) {
        return (root, query, builder) -> {
            if (startDate != null && endDate != null) {
                return builder.between(root.get("dateCreated"), startDate, endDate);
            }
            return null;
        };
    }



    public static Specification<Estate> countryEqual(String country) {
        return (root, query, builder) ->
                country != null ? builder.equal(root.get("country"), country) : null;
    }

    public static Specification<Estate> stateEqual(String state) {
        return (root, query, builder) ->
                state != null ? builder.equal(root.get("state"), state) : null;
    }

    public static Specification<Estate> estateIdEqual(String estateId) {
        return (root, query, builder) ->
                estateId != null ? builder.equal(root.get("estateId"), estateId) : null;
    }

    public static Specification<Estate> cityEqual(String city) {
        return (root, query, builder) ->
                city != null ? builder.equal(root.get("city"), city) : null;
    }

}


