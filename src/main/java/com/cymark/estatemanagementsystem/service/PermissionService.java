package com.cymark.estatemanagementsystem.service;

import com.cymark.estatemanagementsystem.model.dto.PermissionByCategoryDto;
import com.cymark.estatemanagementsystem.model.dto.PermissionDto;
import com.cymark.estatemanagementsystem.model.dto.PermissionRequest;
import com.cymark.estatemanagementsystem.model.response.PaginatedResponse;
import com.cymark.estatemanagementsystem.model.response.Response;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PermissionService {
    PaginatedResponse<List<PermissionDto>> findAll(Pageable pageable);

    PaginatedResponse<List<PermissionByCategoryDto>> findAllByCategory(Pageable pageable);

    Response assignPermission(String phone, String permission);
}
