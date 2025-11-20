package com.cymark.estatemanagementsystem.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    protected String userId;
    protected String firstName;
    protected String lastName;
    protected String middleName;
    protected String email;
    protected String phoneNumber;
    protected String lastLogin;
    protected boolean expiredPassword;
    protected String lastPasswordChange;
    protected boolean enabled;
    protected Integer loginAttempts;
    protected String nationality;
    protected RoleDto role;
    protected String profileImage;
    private String shortBio;
    private String landlordId;
    private String tenantId;
    private String designation;
    private String estate;
    private List<UserDto> subUsersList;
    protected UserFinancialDetails userFinancialDetails;

}
