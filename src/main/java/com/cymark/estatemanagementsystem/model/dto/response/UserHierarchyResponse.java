package com.cymark.estatemanagementsystem.model.dto.response;

import com.cymark.estatemanagementsystem.model.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserHierarchyResponse {
    private UserDto user;
    private String hierarchyType;
    private UserDto parentUser;
    private UserDto landlord;
    private List<UserDto> tenants;
    private List<UserDto> occupants;
    private List<UserDto> children;
    private HierarchyStatistics statistics;
}



