package com.cymark.estatemanagementsystem.model.entity;


import com.cymark.estatemanagementsystem.model.enums.Designation;
import com.cymark.estatemanagementsystem.model.enums.PublishStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;


@Entity
@Getter
@Setter
@Table(name = "product")
public class Product extends BaseEntity {

    @Column(name = "product_id")
    private String productId;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "code")
    private String code;

    @Column(name = "product_image")
    private String productImage;

    @Column(name = "price")
    @Min(value = 0, message = "Value cannot be negative")
    private BigDecimal price;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "estate_id", referencedColumnName = "estate_id")
    private Estate estate;

    @Enumerated(EnumType.STRING)
    @Column(name = "designation")
    private Designation designation;

    @Column(name = "publish_status")
    @Enumerated(EnumType.STRING)
    private PublishStatus publishStatus;
}


