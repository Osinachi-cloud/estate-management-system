package com.cymark.estatemanagementsystem.service;

import com.cymark.estatemanagementsystem.model.dto.AddressDto;
import com.cymark.estatemanagementsystem.model.response.PaginatedResponse;

import java.util.List;

public interface AddressService {
    AddressDto createAddress(AddressDto addressDto);

    PaginatedResponse<List<AddressDto>> fetchAll(int page, int size, String name);
}
