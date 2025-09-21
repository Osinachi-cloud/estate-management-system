package com.cymark.estatemanagementsystem.controller.appAdmin;

import com.cymark.estatemanagementsystem.model.dto.CustomerDto;
import com.cymark.estatemanagementsystem.model.dto.EstateDto;
import com.cymark.estatemanagementsystem.model.dto.request.CustomerRequest;
import com.cymark.estatemanagementsystem.model.response.BaseResponse;
import com.cymark.estatemanagementsystem.security.model.Unsecured;
import com.cymark.estatemanagementsystem.security.service.AuthenticationService;
import com.cymark.estatemanagementsystem.service.EstateService;
import com.cymark.estatemanagementsystem.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.cymark.estatemanagementsystem.util.Constants.BASE_URL;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class EstateController {

    private final EstateService estateService;

    @PostMapping("/onboard-estate")
    public ResponseEntity<BaseResponse<EstateDto>> onboardEstate(@RequestBody @Valid EstateDto estateDto) {
        EstateDto estate = estateService.onboardEstate(estateDto);
        return new ResponseEntity<>(BaseResponse.success(estate, "Estate onboarded successfully"), CREATED);
    }
}
