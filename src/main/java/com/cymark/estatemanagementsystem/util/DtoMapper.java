package com.cymark.estatemanagementsystem.util;

import com.cymark.estatemanagementsystem.model.dto.PermissionByCategoryDto;
import com.cymark.estatemanagementsystem.model.dto.PermissionDto;
import com.cymark.estatemanagementsystem.model.dto.RoleDto;
import com.cymark.estatemanagementsystem.model.dto.UserDto;
import com.cymark.estatemanagementsystem.model.entity.Permission;
import com.cymark.estatemanagementsystem.model.entity.Role;
import com.cymark.estatemanagementsystem.model.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

import static com.cymark.estatemanagementsystem.util.Constants.getStr;

@Slf4j
public class DtoMapper {

    public static RoleDto mapRoleToDto(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        log.info("role permission: {}", role.getPermissions());

        RoleDto roleDto = new RoleDto();
        roleDto.setName(role.getName());
        roleDto.setDescription(role.getDescription());
        roleDto.setDateCreated(role.getDateCreated().toString());
        roleDto.setLastUpdated(role.getLastUpdated().toString());
        roleDto.setPermissionNames(mapToCollectionNamesToString(role.getPermissions()));
        roleDto.setPermissionsDto(mapToCollectionOfPermissionDto(role.getPermissions()));
        return roleDto;
    }

    public static List<String> mapToCollectionNamesToString(Collection<Permission> permissionCollection) {
        List<String> permissionNames = new ArrayList<>();
        for (Permission permission : permissionCollection) {
            if (permission != null) {
                String permissionName = permission.getName();
                permissionNames.add(permissionName);
            }
        }
        return permissionNames;
    }

    public static Collection<PermissionDto> mapToCollectionOfPermissionDto(Collection<Permission> permissionCollection) {
        Collection<PermissionDto> permissionDtos = new ArrayList<>();
        for (Permission permission : permissionCollection) {
            if (permission != null) {
                PermissionDto permissionDto = new PermissionDto();
                permissionDto.setName(permission.getName());
                permissionDto.setId(permission.getId());
                permissionDto.setDescription(permission.getDescription());
                permissionDto.setCategory(permission.getCategory());
                permissionDtos.add(permissionDto);
            }
        }
        return permissionDtos;
    }

    public static RoleDto mapRoleToDtoLoginResponse(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        RoleDto roleDto = new RoleDto();
        roleDto.setName(role.getName());
        roleDto.setDescription(role.getDescription());
//        roleDto.setPermissionsDto(mapToCollectionOfPermissionDto(role.getPermissions()));
        System.out.println("=========== permissions");
        System.out.println(role.getPermissions().size());

        roleDto.setPermissionNames(mapToCollectionOfPermissionDtoLoginResponse(role.getPermissions()));
        return roleDto;
    }

    public static List<String> mapToCollectionOfPermissionDtoLoginResponse(Collection<Permission> permissionCollection) {
        List<String> stringList = new ArrayList<>();

        if (Objects.nonNull(permissionCollection) && !permissionCollection.isEmpty()) {
            for (Permission permission : permissionCollection) {
                log.info("permission_names : {}", permission.getName());
                if (!getStr(permission.getName()).isEmpty()) {
                    stringList.add(permission.getName());
                }
            }
        }


        System.out.println("================= string list");
        System.out.println(stringList);
        return stringList;
    }

    public static List<UserDto> convertUserListToDto(List<UserEntity> userEntityList) {

        return userEntityList.stream().map(userEntity -> {
            UserDto userDto = new UserDto();
            userDto.setFirstName(userEntity.getFirstName());
            userDto.setLastName(userEntity.getLastName());
            userDto.setEmailAddress(userEntity.getEmailAddress());
            userDto.setPhoneNumber(userEntity.getPhoneNumber());
            userDto.setRole(userEntity.getRole().getName());
            userDto.setProfileImage(userEntity.getProfileImage());
            userDto.setShortBio(userEntity.getShortBio());
           return  userDto;
        }).toList();

    }

    public static List<PermissionByCategoryDto> mapToCategoryDto(Collection<Permission> permissionCollection) {
        Map<String, List<PermissionDto>> groupedPermissions = permissionCollection.stream()
                .filter(Objects::nonNull)
                .map(permission -> {
                    PermissionDto permissionDto = new PermissionDto();
                    permissionDto.setName(permission.getName());
                    permissionDto.setId(permission.getId());
                    permissionDto.setDescription(permission.getDescription());
                    permissionDto.setCategory(permission.getCategory());
                    return permissionDto;
                })
                .collect(Collectors.groupingBy(PermissionDto::getCategory));

        return groupedPermissions.entrySet().stream()
                .map(entry -> new PermissionByCategoryDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public static List<RoleDto> mapRoleListToDtoList(List<Role> roleList) {
        return roleList.stream()
                .map(role -> convertRoleToDto(role))
                .collect(Collectors.toList());
    }

    public static RoleDto convertRoleToDto(Role role) {
        RoleDto roleDto = new RoleDto();
        roleDto.setName(role.getName());
        roleDto.setDescription(role.getDescription());
        roleDto.setPermissionNames( mapToCollectionOfPermissionDtoLoginResponse(role.getPermissions()));
        return roleDto;
    }
}
