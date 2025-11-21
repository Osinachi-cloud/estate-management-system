package com.cymark.estatemanagementsystem.model.dto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserStatistics {
    private Long totalUsersCount;
    private Long activeUsersCount;
    private Long inactiveUsersCount;
    private Long landLordUsersCount;
    private Long tenantUsersCount;
    private Long occupantUsersCount;
}
