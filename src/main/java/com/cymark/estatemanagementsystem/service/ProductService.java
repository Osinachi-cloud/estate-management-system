package com.cymark.estatemanagementsystem.service;

import com.cymark.estatemanagementsystem.model.dto.ProductDto;
import com.cymark.estatemanagementsystem.model.response.PaginatedResponse;

import java.util.List;

public interface ProductService {
    ProductDto createProduct(ProductDto productDto);

    PaginatedResponse<List<ProductDto>> fetchProductsBy(int page, int size, String name, String designation);
}
