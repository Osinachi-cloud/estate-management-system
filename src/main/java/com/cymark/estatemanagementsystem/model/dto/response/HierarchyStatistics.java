package com.cymark.estatemanagementsystem.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HierarchyStatistics {
    private Long totalTenants;
    private Long totalOccupants;
    private Long totalProperties;
    private Long activeLeases;
}
