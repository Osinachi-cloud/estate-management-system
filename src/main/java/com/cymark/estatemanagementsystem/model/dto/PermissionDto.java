package com.cymark.estatemanagementsystem.model.dto;

import com.cymark.estatemanagementsystem.model.entity.Permission;
import lombok.Data;

@Data
public class PermissionDto {
    private Long id;

    private String name;

    private String description;

    private String category;
    public PermissionDto(){}
    public PermissionDto(Permission permission){
        this.name = permission.getName();
        this.description = permission.getDescription();
    }
}