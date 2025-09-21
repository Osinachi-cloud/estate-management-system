package com.cymark.estatemanagementsystem.service.impl;

import com.cymark.estatemanagementsystem.exception.CymarkException;
import com.cymark.estatemanagementsystem.model.dto.*;
import com.cymark.estatemanagementsystem.model.dto.request.RoleDtoRequest;
import com.cymark.estatemanagementsystem.model.dto.request.RoleUpdateRequest;
import com.cymark.estatemanagementsystem.model.entity.Permission;
import com.cymark.estatemanagementsystem.model.entity.Role;
import com.cymark.estatemanagementsystem.model.entity.UserEntity;
import com.cymark.estatemanagementsystem.model.response.PaginatedResponse;
import com.cymark.estatemanagementsystem.model.response.Response;
import com.cymark.estatemanagementsystem.repository.PermissionRepository;
import com.cymark.estatemanagementsystem.repository.RoleRepository;
import com.cymark.estatemanagementsystem.repository.UserRepository;
import com.cymark.estatemanagementsystem.service.RoleService;
import com.cymark.estatemanagementsystem.util.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.cymark.estatemanagementsystem.util.Constants.getStr;
import static com.cymark.estatemanagementsystem.util.DtoMapper.*;
import static java.lang.Math.toIntExact;


@Service
public class RoleServiceImpl implements RoleService {

    private final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository adminRepository;

    public RoleServiceImpl(RoleRepository roleRepository, UserRepository adminRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.adminRepository = adminRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Role createUserRole(RoleDto roleDto){
        try {

            Optional<Role> roleOptional = roleRepository.findRoleByName(roleDto.getName());
            if(roleOptional.isPresent()){
                throw new CymarkException("Role already exists");
            }

            log.debug("Creating customer with request: {}", roleDto);
            Role role = new Role();
            role.setName(roleDto.getName());
            role.setDescription(roleDto.getDescription());

            Collection<Permission> permissions = new ArrayList<>();
            Permission permission = new Permission();
            permission.setName(roleDto.getName());
            permission.setDescription(roleDto.getDescription());
            permission.setCategory("CUSTOMER");
            Permission savedPermission = permissionRepository.save(permission);
            log.info("savedPermission : {}", savedPermission);
            permissions.add(savedPermission);
            role.setPermissions(permissions);

            return roleRepository.save(role);
        }catch (Exception e){
            log.error("Error creating role:{}", e.getMessage());
            throw new CymarkException(e.getMessage());
        }

//        return mapRoleToDto(role);
    }

    @Override
    public Optional<Role> findRoleByName(String name){
        System.out.println("in find role method");

        return roleRepository.findRoleByName(getStr(name));
    }


    @Override
    public PaginatedResponse<List<RoleDto>> findAll(Pageable pageable) {
        Page<Role> rolePage = roleRepository.findAll(pageable);
        List<Role> roleList = rolePage.getContent();
        log.info("role List: {}", roleList);

        List<RoleDto> roleDtoList = mapRoleListToDtoList(roleList);

        PaginatedResponse<List<RoleDto>> paginatedResponse = new PaginatedResponse<>();
        paginatedResponse.setPage(rolePage.getNumber());
        paginatedResponse.setData(roleDtoList);
        paginatedResponse.setSize(rolePage.getSize());
        paginatedResponse.setTotal(toIntExact(rolePage.getTotalElements()));

        return paginatedResponse;
    }

    @Override
    public Response assignRole(String roleName, String phoneNumber) {

        UserEntity admin = adminRepository.findByPhone(phoneNumber).orElseThrow(()-> new CymarkException("admin with id:" + phoneNumber + " does not exist"));
        Role role = roleRepository.findByName(roleName).orElseThrow(()-> new CymarkException("role with name:" + roleName + " does not exist"));

        admin.setRole(role);
        adminRepository.save(admin);

        return ResponseUtils.createDefaultSuccessResponse();
    }

    private Collection<Permission> findPermissions(List<Long> ids){
        Collection<Permission> permissionList = new ArrayList<>();
        for(Long id : ids){
            Optional<Permission> permissionExists = permissionRepository.findById(id);
            if(permissionExists.isEmpty()){
                continue;
            }else {
                Permission permission = permissionExists.get();
                permissionList.add(permission);
            }
        }
        return permissionList;
    }

    private Collection<Permission> addPermissions(List<String> names){

        Collection<Permission> permissionList = new ArrayList<>();
        for(String name : names){
            Optional<Permission> permissionExists = permissionRepository.findByName(name);
            if(permissionExists.isEmpty()){
                continue;
            }else {
                Permission permission = permissionExists.get();
                permissionList.add(permission);
            }
        }
        return permissionList;
    }
    @Override
    public RoleDto createRole(RoleDtoRequest roleRequest) {
        Optional<Role> roleExists = roleRepository.findByName(roleRequest.getName());
        if(roleExists.isPresent()){
            throw new CymarkException("Role name already exists");
        }

        if (roleRequest == null) {
            throw new IllegalArgumentException("RoleDto cannot be null.");
        }

        Collection<Permission> permissionList = findPermissions(roleRequest.getIds());

        log.info("Creating role: {}", roleRequest);

        Role role = new Role();
        role.setName(roleRequest.getName());
        role.setDescription(roleRequest.getDescription());
        role.setPermissions(permissionList);

//        Collection<PermissionDto> permissionsDto = mapToCollectionOfPermissionDto(permissionList);

        Role savedRole = roleRepository.save(role);
        log.info("Role created: {}", savedRole);

        return mapRoleToDto(savedRole);
    }

    @Override
    public RoleDto updateRoleAddPermission(String roleName, RoleUpdateRequest roleRequest) {
        Optional<Role> roleExists = roleRepository.findByName(roleName);
        if(roleExists.isEmpty()){
            throw new CymarkException("Role does not exist");
        }

        if (roleRequest == null) {
            throw new IllegalArgumentException("RoleDto cannot be null.");
        }

        Role role = roleExists.get();

//        Collection<Permission> existingPermissions = role.getPermissions();

        Collection<Permission> updatedPermissionList = addPermissions(roleRequest.getPermissionNames());

        log.info("Creating role: {}", roleRequest);

        role.setPermissions(updatedPermissionList);

        Collection<PermissionDto> permissionsDto = mapToCollectionOfPermissionDto(updatedPermissionList);

        Role savedRole = roleRepository.save(role);
        log.info("Role created: {}", savedRole);

        return mapRoleToDto(savedRole);
    }

    @Override
    public Response deleteRole(String name) {
        try {
            Role role = roleRepository.findByName(name).orElseThrow(()-> new CymarkException("role with name: " + name + " does not exist"));
            roleRepository.delete(role);
            return ResponseUtils.createDefaultSuccessResponse();
        } catch (Exception e) {
            log.error("An error occurred: {}", e.getMessage(), e);
            throw e;
        }

    }
}






