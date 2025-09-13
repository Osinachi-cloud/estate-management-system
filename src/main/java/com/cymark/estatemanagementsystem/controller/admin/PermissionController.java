package com.cymark.estatemanagementsystem.controller.admin;

import com.cymark.estatemanagementsystem.exception.CymarkException;
import com.cymark.estatemanagementsystem.model.dto.PermissionByCategoryDto;
import com.cymark.estatemanagementsystem.model.dto.PermissionDto;
import com.cymark.estatemanagementsystem.model.response.PaginatedResponse;
import com.cymark.estatemanagementsystem.model.response.Response;
import com.cymark.estatemanagementsystem.service.PermissionService;
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
public class PermissionController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PreAuthorize("hasAuthority('GET_ALL_PERMISSIONS')")
    @GetMapping(value = "/permissions")
    public PaginatedResponse<List<PermissionDto>> getAllPermissions(@RequestParam Optional<Integer> page,
                                                                    @RequestParam Optional<Integer> size) {
        PageRequest pr = PageRequest.of(page.orElse(0),size.orElse(10));
        return permissionService.findAll(pr);
    }

    @PreAuthorize("hasAuthority('GET_ALL_PERMISSIONS')")
    @GetMapping(value = "get-permission-by-category")
    public PaginatedResponse<List<PermissionByCategoryDto>> findAllPermissionByCategory(@RequestParam Optional<Integer> page,
                                                                                        @RequestParam Optional<Integer> size) {
        PageRequest pr = PageRequest.of(page.orElse(0),size.orElse(10));
        return permissionService.findAllByCategory(pr);
    }

    @PreAuthorize("hasAuthority('ASSIGN_PERMISSION')")
    @PostMapping(value = "/assign-permission")
    public Response assignPermission(@RequestParam(value = "phone-number") String phone, @RequestParam(value = "permission") String permission) {
        try {
            return permissionService.assignPermission(phone, permission);
        } catch (CymarkException exception) {
            throw exception;
        }
        catch (Exception e) {
            log.error("Error assigning role: {}" ,phone + " " + permission, e);

            throw new CymarkException();
        }
    }
}