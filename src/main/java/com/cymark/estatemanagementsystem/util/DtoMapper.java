package com.cymark.estatemanagementsystem.util;

import com.cymark.estatemanagementsystem.model.dto.*;
import com.cymark.estatemanagementsystem.model.dto.request.OrderRequest;
import com.cymark.estatemanagementsystem.model.entity.*;
import com.cymark.estatemanagementsystem.model.enums.Designation;
import com.cymark.estatemanagementsystem.model.enums.OrderStatus;
import lombok.extern.slf4j.Slf4j;

import javax.print.attribute.standard.Destination;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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

    public static RoleDto mapRoleToDtoWithoutPermissionDto(Role role) {
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
            userDto.setEmail(userEntity.getEmail());
            userDto.setPhoneNumber(userEntity.getPhone());
            userDto.setRole(convertRoleToDto(userEntity.getRole()));
            userDto.setProfileImage(userEntity.getProfileImage());
            userDto.setShortBio(userEntity.getShortBio());
            userDto.setLandlordId(userEntity.getLandlordId());
            userDto.setTenantId(userEntity.getTenantId());
            userDto.setDesignation(userEntity.getDesignation().toString());
            userDto.setUserId(userEntity.getUserId());
            userDto.setEnabled(userEntity.isEnabled());
           return  userDto;
        }).toList();

    }

    public static List<EstateDto> convertEstateListToDto(List<Estate> estateList) {

        return estateList.stream().map(userEntity -> {
            EstateDto estateDto = new EstateDto();
            estateDto.setCountry(userEntity.getCountry());
            estateDto.setState(userEntity.getState());
            estateDto.setCity(userEntity.getCity());
            estateDto.setEstateId(userEntity.getEstateId());
            return  estateDto;
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

    public static List<ProductDto> convertProductListToDto(List<Product> productList) {
        return productList.stream().map(ProductDto::new).toList();

    }

    public static List<OrderDto> orderListToDto(List<Order> productOrderList) {

        List<OrderDto> productOrderDtoList = new ArrayList<>();

        for(Order order : productOrderList){
            productOrderDtoList.add(convertProductOrderToDto(order));
        }
        return productOrderDtoList;
    }


    public static OrderDto convertProductOrderToDto(Order productOrder) {
        log.info("productOrder : {}", productOrder);
        OrderDto productOrderDto = new OrderDto();
        productOrderDto.setStatus(productOrder.getStatus());
        productOrderDto.setReferenceNumber(productOrder.getOrderId());
        productOrderDto.setProductName(productOrder.getProductName());
        productOrderDto.setOrderId(productOrder.getOrderId());
        productOrderDto.setCustomerId(productOrder.getEmailAddress());
        productOrderDto.setCurrency(productOrder.getCurrency());
        productOrderDto.setPaymentMode(productOrder.getPaymentMode());
        productOrderDto.setAmount(productOrder.getAmount());
        productOrderDto.setCustomerId(productOrder.getEmailAddress());
        productOrderDto.setDateCreated(formattedDate(productOrder.getDateCreated()));
        productOrderDto.setCurrency(productOrder.getCurrency());
        productOrderDto.setQuantity(productOrder.getQuantity());

        log.info("productOrderDto ----------======>: {}", productOrderDto);
        return productOrderDto;
    }

    public static String formattedDate(Instant dateCreated){

        try{
            Instant instant = Instant.parse(dateCreated.toString());
            ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("UTC"));
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy - hh:mm a");
            return zonedDateTime.format(outputFormatter);
        }catch (Exception exception){
            throw new RuntimeException(exception.getMessage());
        }
    }

    public static Order convertRequestToModel(OrderRequest productOrderRequest) {
        Order productOrder = new Order();
        productOrder.setOrderId(productOrderRequest.getOrderId());
        productOrder.setEmailAddress(productOrderRequest.getEmailAddress());
        productOrder.setProductId(productOrderRequest.getProductId());
        productOrder.setProductName(productOrderRequest.getProductName());
        productOrder.setPaymentMode(productOrderRequest.getPaymentMode());
        productOrder.setCurrency(productOrderRequest.getCurrency());
        productOrder.setAmount(productOrderRequest.getAmount());
        productOrder.setStatus(OrderStatus.valueOf(productOrderRequest.getStatus()));
        productOrder.setTransactionId(productOrderRequest.getTransactionId());
        productOrder.setQuantity(productOrderRequest.getQuantity());
        productOrder.setCurrency(productOrderRequest.getCurrency());
        productOrder.setSubscribeFor(convertToLocalDate(productOrderRequest.getSubscribeFor()));
        productOrder.setEstateId(productOrderRequest.getEstateId());
        productOrder.setDesignation(Designation.valueOf(productOrderRequest.getDesignation()));

        log.info("productOrder : {}",productOrder);
        return productOrder;
    }

    public static LocalDateTime convertToLocalDate(String isoString) {
        return LocalDateTime.parse(isoString, DateTimeFormatter.ISO_DATE_TIME);
    }


}
