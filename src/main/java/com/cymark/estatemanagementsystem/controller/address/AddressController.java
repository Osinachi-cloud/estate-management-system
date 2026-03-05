package com.cymark.estatemanagementsystem.controller.address;

import com.cymark.estatemanagementsystem.model.dto.AddressDto;
import com.cymark.estatemanagementsystem.model.dto.ProductDto;
import com.cymark.estatemanagementsystem.model.response.BaseResponse;
import com.cymark.estatemanagementsystem.model.response.PaginatedResponse;
import com.cymark.estatemanagementsystem.security.model.Unsecured;
import com.cymark.estatemanagementsystem.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cymark.estatemanagementsystem.util.Constants.BASE_URL;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

//    @PreAuthorize("hasAnyAuthority('ONBOARD_ESTATE')")
    @PostMapping("/create-address")
    public ResponseEntity<BaseResponse<AddressDto>> createAddress(@RequestBody @Valid AddressDto addressRequest) {
        AddressDto address = addressService.createAddress(addressRequest);
        return new ResponseEntity<>(BaseResponse.success(address, "Address created successfully"), CREATED);
    }

    @Unsecured
    @GetMapping("/get-addresses")
    public ResponseEntity<BaseResponse<PaginatedResponse<List<AddressDto>>>> getAddresses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String estateId) {

        PaginatedResponse<List<AddressDto>> addresses = addressService.fetchAll(page, size, estateId);
        return ResponseEntity.ok(BaseResponse.success(addresses, "Products retrieved successfully"));
    }

}
