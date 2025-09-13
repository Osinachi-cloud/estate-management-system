package com.cymark.estatemanagementsystem.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PermissionRequest {
    private String phone;
    private String permission;
}
