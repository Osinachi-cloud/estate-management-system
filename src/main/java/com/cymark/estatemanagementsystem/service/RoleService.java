package com.cymark.estatemanagementsystem.service;

import com.cymark.estatemanagementsystem.model.dto.RoleDto;
import com.cymark.estatemanagementsystem.model.dto.request.RoleDtoRequest;
import com.cymark.estatemanagementsystem.model.dto.request.RoleUpdateRequest;
import com.cymark.estatemanagementsystem.model.entity.Role;
import com.cymark.estatemanagementsystem.model.response.PaginatedResponse;
import com.cymark.estatemanagementsystem.model.response.Response;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    Role createUserRole(RoleDto roleDto);
    Optional<Role> findRoleByName(String name);

    PaginatedResponse<List<RoleDto>> findAll(Pageable pageable);

    Response assignRole(String roleName, String phoneNumber);

    RoleDto createRole(RoleDtoRequest roleRequest);

    RoleDto updateRoleAddPermission(String roleName, RoleUpdateRequest roleRequest);

    Response deleteRole(String name);
}
