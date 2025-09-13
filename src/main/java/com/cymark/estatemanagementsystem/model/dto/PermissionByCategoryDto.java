package com.cymark.estatemanagementsystem.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class PermissionByCategoryDto {
    private String category;
    private List<PermissionDto> permissions;

    public PermissionByCategoryDto(String category, List<PermissionDto> permissions) {
        this.category = category;
        this.permissions = permissions;
    }
}