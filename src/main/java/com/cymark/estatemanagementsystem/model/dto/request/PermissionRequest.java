package com.cymark.estatemanagementsystem.model.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PermissionRequest {
    private String phone;
    private String permission;
}
