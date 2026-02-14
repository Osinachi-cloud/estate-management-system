package com.cymark.estatemanagementsystem.controller.user;

import com.cymark.estatemanagementsystem.model.dto.response.DetailedUserViewResponse;
import com.cymark.estatemanagementsystem.model.dto.response.UserHierarchyResponse;
import com.cymark.estatemanagementsystem.model.response.BaseResponse;
import com.cymark.estatemanagementsystem.service.UserHierarchyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.cymark.estatemanagementsystem.util.Constants.BASE_URL;

@RequiredArgsConstructor
@RestController
@RequestMapping(BASE_URL)
//@CrossOrigin(origins = "*")
public class UserHierarchyController {

    private final UserHierarchyService userHierarchyService;

    @GetMapping("/hierarchy")
    public ResponseEntity<BaseResponse<UserHierarchyResponse>> getUserHierarchy(@RequestParam String userId) {
        try {
            UserHierarchyResponse response = userHierarchyService.getUserHierarchy(userId);
            return ResponseEntity.ok(BaseResponse.success(response, "User hierarchy fetched successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.error(HttpStatus.BAD_REQUEST, e.getMessage()));
        }
    }

    @GetMapping("/get-user-detailed-view")
    public ResponseEntity<BaseResponse<DetailedUserViewResponse>> getDetailedUserView(@RequestParam String userId) {
        // No decoding needed
        try {
            DetailedUserViewResponse response = userHierarchyService.getDetailedUserView(userId);
            return ResponseEntity.ok(BaseResponse.success(response, "User detailed view fetched successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.error(HttpStatus.BAD_REQUEST ,e.getMessage()));
        }
    }
}