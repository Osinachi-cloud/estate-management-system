package com.cymark.estatemanagementsystem.model.response;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedResponse <T> {
    private int page;
    private int size;
    private int total;
    private T data;
}
