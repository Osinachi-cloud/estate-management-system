package com.cymark.estatemanagementsystem.model.dto;

import com.cymark.estatemanagementsystem.model.entity.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.cymark.estatemanagementsystem.util.DtoMapper.mapToCollectionOfPermissionDto;

@Data
@NoArgsConstructor
public class RoleDto {

    private String name;

    private String description;

    private String dateCreated;

    private String lastUpdated;

    private List<String> permissionNames = new ArrayList<>();

    private Collection<PermissionDto> permissionsDto;

    public RoleDto(Role role) {
        this.name = role.getName();
        this.description = role.getDescription();
        this.dateCreated = role.getDateCreated().toString();
        this.lastUpdated = role.getLastUpdated().toString();
        this.permissionsDto = mapToCollectionOfPermissionDto(role.getPermissions());
    }

    public RoleDto(String name, String description) {
        this.name = name;
        this.description = description;
    }
}