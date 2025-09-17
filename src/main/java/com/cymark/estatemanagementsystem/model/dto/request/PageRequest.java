package com.cymark.estatemanagementsystem.model.dto.request;

import lombok.Data;

@Data
public class PageRequest {
    private int page;
    private int size;
}
