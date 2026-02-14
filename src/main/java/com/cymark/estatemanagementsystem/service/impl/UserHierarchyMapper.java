package com.cymark.estatemanagementsystem.service.impl;

import com.cymark.estatemanagementsystem.model.dto.*;
import com.cymark.estatemanagementsystem.model.entity.*;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.cymark.estatemanagementsystem.util.DtoMapper.mapRoleToDto;

@Component
public class UserHierarchyMapper {

    public UserDto toUserDto(UserEntity user) {
        if (user == null) return null;
        
        return UserDto.builder()
//                .id(user.getId())
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhone())
                .profileImage(user.getProfileImage())
//                .estate(user.getEstate())
//                .pinAttempts(user.getPinAttempts())
//                .country(user.getCountry())
//                .referredBy(user.getReferredBy())
//                .shortBio(user.getShortBio())
//                .saveCard(user.isSaveCard())
//                .enablePush(user.isEnablePush())
                .landlordId(user.getLandlordId())
                .tenantId(user.getTenantId())
//                .occupantId(user.getOccupantId())
//                .isOccupancyVerified(user.isOccupancyVerified())
                .designation(user.getDesignation().name())
                .enabled(user.isEnabled())
                .role(mapRoleToDto(user.getRole()))
//                .addresses(toAddressDtoCollection(user.getAddresses()))
//                .create(user.getDateCreated())
//                .lastUpdated(user.getLastUpdated())
                .build();
    }

//    public RoleDto toRoleDto(Role role) {
//        if (role == null) return null;
//
//        return RoleDto.builder()
//                .id(role.getId())
//                .name(role.getName())
//                .description(role.getDescription())
//                .build();
//    }

//    public AddressDto toAddressDto(Address address) {
//        if (address == null) return null;
//
//        return AddressDto.builder()
//                .id(address.getId())
//                .street(address.getStreet())
//                .houseNumber(address.getHouseNumber())
//                .apartmentNumber(address.getApartmentNumber())
//                .postalCode(address.getPostalCode())
//                .fullAddress(address.getFullAddress())
//                .estate(toEstateDto(address.getEstate()))
//                .build();
//    }

//    public EstateDto toEstateDto(Estate estate) {
//        if (estate == null) return null;
//
//        return EstateDto.builder()
//                .estateId(estate.getEstateId())
//                .name(estate.getName())
//                .location(estate.getLocation())
//                .build();
//    }

//    public Collection<AddressDto> toAddressDtoCollection(Collection<Address> addresses) {
//        if (addresses == null) return null;
//        return addresses.stream()
//                .map(this::toAddressDto)
//                .collect(Collectors.toList());
//    }

    public List<UserDto> toUserDtoList(List<UserEntity> users) {
        if (users == null) return null;
        return users.stream()
                .map(this::toUserDto)
                .collect(Collectors.toList());
    }
}