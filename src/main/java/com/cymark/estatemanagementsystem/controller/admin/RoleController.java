package com.cymark.estatemanagementsystem.controller.admin;

import com.cymark.estatemanagementsystem.exception.CymarkException;
import com.cymark.estatemanagementsystem.model.dto.RoleDto;
import com.cymark.estatemanagementsystem.model.dto.RoleDtoRequest;
import com.cymark.estatemanagementsystem.model.dto.RoleUpdateRequest;
import com.cymark.estatemanagementsystem.model.response.PaginatedResponse;
import com.cymark.estatemanagementsystem.model.response.Response;
import com.cymark.estatemanagementsystem.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
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
    @GetMapping(value = "/roles")
    public PaginatedResponse<List<RoleDto>> getAllRoles(@RequestParam Optional<Integer> page,
                                                        @RequestParam Optional<Integer> size) {
        PageRequest pr = PageRequest.of(page.orElse(0),size.orElse(10));
        return roleService.findAll(pr);
    }

    @PreAuthorize("hasAuthority('ASSIGN_ROLE')")
    @PostMapping(value = "/assign-role")
    public Response assignRole(@RequestParam String role, @RequestParam (name = "phone-number") String phoneNumber) {
        try {
            return roleService.assignRole(role, phoneNumber);
        } catch (CymarkException exception) {
            throw exception;
        }
        catch (Exception e) {
            log.error("Error assigning role: {}" ,phoneNumber + " " +  role, e);
            throw new CymarkException(e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('CREATE_ROLE')")
    @PostMapping(value = "/create-role")
    public RoleDto createRole(@RequestBody RoleDtoRequest roleRequest) {
        try {
            return roleService.createRole(roleRequest);
        } catch (CymarkException exception) {
            throw exception;
        }
        catch (Exception e) {
            log.error("Error creating role: {}" ,roleRequest, e);
            throw new CymarkException(e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('UPDATE_ROLE_ADD_PERMISSION')")
    @PatchMapping(value = "/update-role-add-permission")
    public RoleDto updateRoleAddPermission(
            @RequestParam("role-name") String roleName,
            @RequestBody RoleUpdateRequest roleRequest) {
        try {
            return roleService.updateRoleAddPermission(roleName,roleRequest);
        } catch (CymarkException exception) {
            throw exception;
        }
        catch (Exception e) {
            log.error("Error creating role: {}" ,roleRequest, e);
            throw new CymarkException(e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('DELETE_ROLE')")
    @PostMapping(value = "/delete-role")
    public Response deleteRole(@RequestParam("name") String name) {
        return roleService.deleteRole(name);
    }
}