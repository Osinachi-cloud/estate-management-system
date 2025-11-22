package com.cymark.estatemanagementsystem.controller.productController;

import com.cymark.estatemanagementsystem.model.dto.EstateDto;
import com.cymark.estatemanagementsystem.model.dto.ProductDto;
import com.cymark.estatemanagementsystem.model.response.BaseResponse;
import com.cymark.estatemanagementsystem.model.response.PaginatedResponse;
import com.cymark.estatemanagementsystem.security.model.Unsecured;
import com.cymark.estatemanagementsystem.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cymark.estatemanagementsystem.util.Constants.BASE_URL;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class ProductController {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private final ProductService productService;

    @PostMapping("/create-product")
    public ResponseEntity<BaseResponse<ProductDto>> createProduct(@RequestBody ProductDto productRequest) {
        log.info("Creating product controller {}", productRequest);
        ProductDto productDto = productService.createProduct(productRequest);
        return new ResponseEntity<>(BaseResponse.success(productDto, "Product created successfully"), CREATED);
    }

    @Unsecured
    @GetMapping("/get-products")
    public ResponseEntity<BaseResponse<PaginatedResponse<List<ProductDto>>>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String designation) {

        PaginatedResponse<List<ProductDto>> products = productService.fetchProductsBy(page, size, name, designation, null);
        return ResponseEntity.ok(BaseResponse.success(products, "Products retrieved successfully"));
    }

    @Unsecured
    @GetMapping("/get-products-published")
    public ResponseEntity<BaseResponse<PaginatedResponse<List<ProductDto>>>> getPublishedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String designation) {

        PaginatedResponse<List<ProductDto>> products = productService.fetchProductsBy(page, size, name, designation, true);
        return ResponseEntity.ok(BaseResponse.success(products, "Products retrieved successfully"));
    }
}
