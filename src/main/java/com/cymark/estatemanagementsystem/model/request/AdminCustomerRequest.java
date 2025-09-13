package com.cymark.estatemanagementsystem.model.request;

import com.cymark.estatemanagementsystem.model.enums.Designation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminCustomerRequest {


    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @NotBlank(message = "Email address cannot be blank")
    @Pattern(regexp = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$",
            message = "Invalid email format")
    private String emailAddress;

    @NotNull(message = "Phone number can not be null")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$",
            message = "Invalid phone number format. Use international format: +1234567890")
    private String phoneNumber;

    @NotBlank(message = "designation cannot be blank")
    private Designation designation;

}
