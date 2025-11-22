package com.cymark.estatemanagementsystem.model.dto;

import com.cymark.estatemanagementsystem.model.entity.Estate;
import com.cymark.estatemanagementsystem.model.entity.Product;
import com.cymark.estatemanagementsystem.model.enums.Designation;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDto {
    private Long productId;
    private String name;
    private String description;
    private String code;
    private String productImage;
    @Min(value = 0, message = "Value cannot be negative")
    private BigDecimal price;
    private String designation;
    private Boolean publishStatus;
    private BigDecimal transactionCharge;
    private Estate estate;


    public ProductDto() {}

    public ProductDto(Product product) {
        productId = product.getId();
        name = product.getName();
        description = product.getDescription();
        code = product.getCode();
        productImage = product.getProductImage();
        price = product.getPrice();
        estate = product.getEstate();
        designation = product.getDesignation().toString();
        publishStatus = product.getPublishStatus();
        transactionCharge = product.getTransactionCharge();
    }
}
