package com.cymark.estatemanagementsystem.controller.admin;

import com.cymark.estatemanagementsystem.exception.CymarkException;
import com.cymark.estatemanagementsystem.model.dto.PermissionByCategoryDto;
import com.cymark.estatemanagementsystem.model.dto.PermissionDto;
import com.cymark.estatemanagementsystem.model.response.BaseResponse;
import com.cymark.estatemanagementsystem.model.response.PaginatedResponse;
import com.cymark.estatemanagementsystem.model.response.Response;
import com.cymark.estatemanagementsystem.service.PermissionService;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
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

//    @PreAuthorize("hasAuthority('GET_ALL_PERMISSIONS')")
    @GetMapping("/permissions")
    public ResponseEntity<BaseResponse<PaginatedResponse<List<PermissionDto>>>> getAllPermissions(
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<Integer> size) {

        PageRequest pr = PageRequest.of(page.orElse(0), size.orElse(10));
        PaginatedResponse<List<PermissionDto>> permissions = permissionService.findAll(pr);
        return ResponseEntity.ok(BaseResponse.success(permissions, "Permissions retrieved successfully"));
    }

//    @PreAuthorize("hasAuthority('GET_ALL_PERMISSIONS')")
    @GetMapping("/get-permission-by-category")
    public ResponseEntity<BaseResponse<PaginatedResponse<List<PermissionByCategoryDto>>>> findAllPermissionByCategory(
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<Integer> size) {

        PageRequest pr = PageRequest.of(page.orElse(0), size.orElse(10));
        PaginatedResponse<List<PermissionByCategoryDto>> permissions = permissionService.findAllByCategory(pr);
        return ResponseEntity.ok(BaseResponse.success(permissions, "Permissions by category retrieved successfully"));
    }

//    @PreAuthorize("hasAuthority('ASSIGN_PERMISSION')")
    @PostMapping("/assign-permission")
    public ResponseEntity<BaseResponse<Response>> assignPermission(
            @RequestParam(value = "phoneNumber") @NotBlank String phone,
            @RequestParam(value = "permission") @NotBlank String permission) {

        try {
            Response response = permissionService.assignPermission(phone, permission);
            return ResponseEntity.ok(BaseResponse.success(response, "Permission assigned successfully"));
        } catch (CymarkException exception) {
            throw exception;
        } catch (Exception e) {
            log.error("Error assigning permission: {} to phone: {}", permission, phone, e);
            throw new CymarkException("Failed to assign permission: " + e.getMessage());
        }
    }
}