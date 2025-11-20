package com.cymark.estatemanagementsystem.util;

import com.cymark.estatemanagementsystem.model.dto.ProductDto;
import com.cymark.estatemanagementsystem.model.dto.RoleDto;
import com.cymark.estatemanagementsystem.model.entity.Product;
import com.cymark.estatemanagementsystem.model.entity.Role;
import com.cymark.estatemanagementsystem.model.enums.Designation;
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

    public static Product convertDtoToProduct(ProductDto source){
        Product destination = new Product();
        destination.setName(source.getName());
        destination.setDescription(source.getDescription());
        destination.setPrice(source.getPrice());
        destination.setDesignation(Designation.valueOf(source.getDesignation()));
        destination.setCode(source.getCode());
        destination.setEstate(source.getEstate());
        destination.setProductImage(source.getProductImage());
        return destination;
    }


}
