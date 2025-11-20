package com.cymark.estatemanagementsystem.model.dto;

import com.cymark.estatemanagementsystem.model.entity.Estate;
import com.cymark.estatemanagementsystem.model.entity.UserEntity;
import com.cymark.estatemanagementsystem.model.enums.Designation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EstateDto {

    @NotBlank(message = "Country cannot be blank")
    private String country;

    @NotBlank(message = "State cannot be blank")
    private String state;

    @NotBlank(message = "City cannot be blank")
    private String city;

    @NotBlank(message = "Name cannot be blank")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Postal code cannot be blank")
    @Pattern(regexp = "^[0-9]{4,10}$", message = "Invalid postal code format")
    private String postalCode;

    @NotBlank(message = "Estate ID cannot be blank")
    private String estateId;

    @NotBlank(message = "Estate admin user ID cannot be blank")
    private String estateAdminUserId;

    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @NotNull(message = "Phone number cannot be null")
    @Pattern(
            regexp = "^\\+?[1-9]\\d{1,14}$",
            message = "Invalid phone number format. Use international format: +1234567890"
    )
    private String phone;

    @NotBlank(message = "Email cannot be blank")
    @Pattern(
            regexp = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$",
            message = "Invalid email format"
    )
    private String email;

    @NotNull(message = "Designation cannot be null")
    private Designation designation;

    public EstateDto(Estate estate, UserEntity user) {
        if (estate != null && user != null) {
            country = estate.getCountry();
            state = estate.getState();
            city = estate.getCity();
            name = estate.getName();
            postalCode = estate.getPostalCode();
            estateAdminUserId = user.getUserId();
            firstName = user.getFirstName();
            lastName = user.getLastName();
            phone = user.getPhone();
            email = user.getEmail();
        }
    }
    public EstateDto() {

    }


//    public EstateDto() {
//        if (estate != null && user != null) {
//            country = estate.getCountry();
//            state = estate.getState();
//            city = estate.getCity();
//            name = estate.getName();
//            postalCode = estate.getPostalCode();
//            estateAdminUserId = user.getUserId();
//            firstName = user.getFirstName();
//            lastName = user.getLastName();
//            phone = user.getPhone();
//            email = user.getEmail();
//        }
//    }
}
