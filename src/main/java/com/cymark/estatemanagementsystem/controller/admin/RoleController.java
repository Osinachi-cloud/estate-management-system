package com.cymark.estatemanagementsystem.controller.admin;

import com.cymark.estatemanagementsystem.exception.CymarkException;
import com.cymark.estatemanagementsystem.model.dto.RoleDto;
import com.cymark.estatemanagementsystem.model.dto.request.RoleDtoRequest;
import com.cymark.estatemanagementsystem.model.dto.request.RoleUpdateRequest;
import com.cymark.estatemanagementsystem.model.response.BaseResponse;
import com.cymark.estatemanagementsystem.model.response.PaginatedResponse;
import com.cymark.estatemanagementsystem.model.response.Response;
import com.cymark.estatemanagementsystem.service.RoleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.cymark.estatemanagementsystem.util.Constants.BASE_URL;

@RestController
@RequestMapping(BASE_URL)
public class RoleController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }


    @PreAuthorize("hasAuthority('GET_ALL_ROLES')")
    @GetMapping("/roles")
    public ResponseEntity<BaseResponse<PaginatedResponse<List<RoleDto>>>> getAllRoles(
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<Integer> size) {

        PageRequest pr = PageRequest.of(page.orElse(0), size.orElse(10));
        PaginatedResponse<List<RoleDto>> roles = roleService.findAll(pr);
        return ResponseEntity.ok(BaseResponse.success(roles, "Roles retrieved successfully"));
    }

    @PreAuthorize("hasAuthority('ASSIGN_ROLE')")
    @PostMapping("/assign-role")
    public ResponseEntity<BaseResponse<Response>> assignRole(
            @RequestParam @NotBlank String role,
            @RequestParam(name = "phone-number") @NotBlank String phoneNumber) {

        try {
            Response response = roleService.assignRole(role, phoneNumber);
            return ResponseEntity.ok(BaseResponse.success(response, "Role assigned successfully"));
        } catch (CymarkException exception) {
            throw exception;
        } catch (Exception e) {
            log.error("Error assigning role: {} to phone: {}", role, phoneNumber, e);
            throw new CymarkException("Failed to assign role: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('CREATE_ROLE')")
    @PostMapping("/create-role")
    public ResponseEntity<BaseResponse<RoleDto>> createRole(@RequestBody @Valid RoleDtoRequest roleRequest) {

        try {
            RoleDto role = roleService.createRole(roleRequest);
            return new ResponseEntity<>(BaseResponse.success(role, "Role created successfully"), HttpStatus.CREATED);
        } catch (CymarkException exception) {
            throw exception;
        } catch (Exception e) {
            log.error("Error creating role: {}", roleRequest, e);
            throw new CymarkException("Failed to create role: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('UPDATE_ROLE_ADD_PERMISSION')")
    @PatchMapping("/update-role-add-permission")
    public ResponseEntity<BaseResponse<RoleDto>> updateRoleAddPermission(
            @RequestParam("role-name") @NotBlank String roleName,
            @RequestBody @Valid RoleUpdateRequest roleRequest) {

        try {
            RoleDto updatedRole = roleService.updateRoleAddPermission(roleName, roleRequest);
            return ResponseEntity.ok(BaseResponse.success(updatedRole, "Role permissions updated successfully"));
        } catch (CymarkException exception) {
            throw exception;
        } catch (Exception e) {
            log.error("Error updating role permissions: {} for role: {}", roleRequest, roleName, e);
            throw new CymarkException("Failed to update role permissions: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('DELETE_ROLE')")
    @PostMapping("/delete-role")
    public ResponseEntity<BaseResponse<Response>> deleteRole(@RequestParam("name") @NotBlank String name) {
        Response response = roleService.deleteRole(name);
        return ResponseEntity.ok(BaseResponse.success(response, "Role deleted successfully"));
    }

}