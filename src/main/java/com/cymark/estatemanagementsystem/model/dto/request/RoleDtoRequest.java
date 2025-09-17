package com.cymark.estatemanagementsystem.model.dto.request;

import lombok.Data;

import java.util.List;


@Data
public class RoleDtoRequest {

    private String name;

    private String description;

    private List<Long> ids;
}