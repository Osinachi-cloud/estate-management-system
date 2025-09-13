package com.cymark.estatemanagementsystem.util;

import com.cymark.estatemanagementsystem.model.dto.RoleDto;
import com.cymark.estatemanagementsystem.model.entity.Role;
import org.springframework.beans.BeanUtils;

import java.util.Objects;

public class Mapper {

    public static Object convertModelToDto(Object source, Object destination){
        BeanUtils.copyProperties(source, destination);
        return destination;
    }

    public static RoleDto convertModelToDto(Role role){
        RoleDto dto = new RoleDto();
        BeanUtils.copyProperties(role, dto);
        return dto;
    }


}
