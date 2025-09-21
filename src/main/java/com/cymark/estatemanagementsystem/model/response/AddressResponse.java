package com.cymark.estatemanagementsystem.model.response;

import com.cymark.estatemanagementsystem.model.dto.EstateDto;
import com.cymark.estatemanagementsystem.model.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponse {

    private String country;
    private String state;
    private String city;
    private String street;
    private String houseNumber;
    private String apartmentNumber;
    private String postalCode;
    private String fullAddress;
    private EstateDto estateDto;
    private UserDto userDto;
}


