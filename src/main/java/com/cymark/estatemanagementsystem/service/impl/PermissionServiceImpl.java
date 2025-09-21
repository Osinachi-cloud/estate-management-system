package com.cymark.estatemanagementsystem.service.impl;

import com.cymark.estatemanagementsystem.exception.CymarkException;
import com.cymark.estatemanagementsystem.exception.UserNotFoundException;
import com.cymark.estatemanagementsystem.model.dto.PermissionByCategoryDto;
import com.cymark.estatemanagementsystem.model.dto.PermissionDto;
import com.cymark.estatemanagementsystem.model.entity.Permission;
import com.cymark.estatemanagementsystem.model.entity.Role;
import com.cymark.estatemanagementsystem.model.entity.UserEntity;
import com.cymark.estatemanagementsystem.model.response.PaginatedResponse;
import com.cymark.estatemanagementsystem.model.response.Response;
import com.cymark.estatemanagementsystem.repository.PermissionRepository;
import com.cymark.estatemanagementsystem.repository.RoleRepository;
import com.cymark.estatemanagementsystem.repository.UserRepository;
import com.cymark.estatemanagementsystem.service.PermissionService;
import com.cymark.estatemanagementsystem.util.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//import static com.exquisapps.billantedadmin.utils.AdminUtils.mapToCategoryDto;
//import static com.exquisapps.billantedadmin.utils.AdminUtils.mapToCollectionOfPermissionDto;
import static com.cymark.estatemanagementsystem.util.DtoMapper.mapToCategoryDto;
import static com.cymark.estatemanagementsystem.util.DtoMapper.mapToCollectionOfPermissionDto;
import static java.lang.Math.toIntExact;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final Logger log = LoggerFactory.getLogger(PermissionServiceImpl.class);
    private final PermissionRepository permissionRepository;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository, UserRepository userRepository, RoleRepository roleRepository) {
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public PaginatedResponse<List<PermissionDto>> findAll(Pageable pageable) {
        Page<Permission> permissionPage = permissionRepository.findAll(pageable);
        List<Permission> permissionList = permissionPage.getContent();
        List<PermissionDto> permissionDtoList = new ArrayList<>(mapToCollectionOfPermissionDto(permissionList));

        PaginatedResponse<List<PermissionDto>> paginatedResponse = new PaginatedResponse<>();
        paginatedResponse.setPage(permissionPage.getNumber());
        paginatedResponse.setData(permissionDtoList);
        paginatedResponse.setSize(permissionPage.getSize());
        paginatedResponse.setTotal(toIntExact(permissionPage.getTotalElements()));

        return paginatedResponse;
    }

    @Override
    public PaginatedResponse<List<PermissionByCategoryDto>> findAllByCategory(Pageable pageable) {
        Page<Permission> permissionPage = permissionRepository.findAll(pageable);
        List<Permission> permissionList = permissionPage.getContent();
        List<PermissionByCategoryDto> permissionDtoList = new ArrayList<>(mapToCategoryDto(permissionList));

        PaginatedResponse<List<PermissionByCategoryDto>> paginatedResponse = new PaginatedResponse<>();
        paginatedResponse.setPage(permissionPage.getNumber());
        paginatedResponse.setData(permissionDtoList);
        paginatedResponse.setSize(permissionPage.getSize());
        paginatedResponse.setTotal(permissionDtoList.size());

        return paginatedResponse;
    }

    @Override
    public Response assignPermission(String phone, String permissionName) {

        UserEntity admin = userRepository.findByPhone(phone).orElseThrow(()-> new UserNotFoundException("admin with id:" + phone + " does not exist"));
        Permission permission = permissionRepository.findByName(permissionName).orElseThrow(()-> new CymarkException("permission with name:" + permissionName + " does not exist"));

        Role role = admin.getRole();
        Collection<Permission> permissionList = admin.getRole().getPermissions();
        permissionList.add(permission);
        role.setPermissions(permissionList);
        roleRepository.save(role);
        admin.setRole(role);
        userRepository.save(admin);

        return ResponseUtils.createDefaultSuccessResponse();

    }
}
