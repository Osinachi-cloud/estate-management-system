package com.cymark.estatemanagementsystem.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoleUpdateRequest {

    private List<String> permissionNames;

}