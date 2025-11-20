package com.cymark.estatemanagementsystem.controller.appAdmin;

import com.cymark.estatemanagementsystem.model.dto.EstateDto;
import com.cymark.estatemanagementsystem.model.dto.UserDto;
import com.cymark.estatemanagementsystem.model.response.BaseResponse;
import com.cymark.estatemanagementsystem.model.response.PaginatedResponse;
import com.cymark.estatemanagementsystem.security.model.Unsecured;
import com.cymark.estatemanagementsystem.service.EstateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cymark.estatemanagementsystem.util.Constants.BASE_URL;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class EstateController {

    private final EstateService estateService;

    @PreAuthorize("hasAnyAuthority('ONBOARD_ESTATE')")
    @PostMapping("/onboard-estate")
    public ResponseEntity<BaseResponse<EstateDto>> onboardEstate(@RequestBody @Valid EstateDto estateDto) {
        EstateDto estate = estateService.onboardEstate(estateDto);
        return new ResponseEntity<>(BaseResponse.success(estate, "Estate onboarded successfully"), CREATED);
    }

    @Unsecured
    @GetMapping("/get-estates")
    public ResponseEntity<BaseResponse<PaginatedResponse<List<EstateDto>>>> getEstates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String estateId) {

        PaginatedResponse<List<EstateDto>> users = estateService.fetchAllEstatessBy(page, size, country, state, city, estateId);
        return ResponseEntity.ok(BaseResponse.success(users, "Estate retrieved successfully"));
    }
}
