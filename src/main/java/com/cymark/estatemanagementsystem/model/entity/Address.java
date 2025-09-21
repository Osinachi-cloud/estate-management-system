package com.cymark.estatemanagementsystem.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.Audited;

import java.util.Collection;

@Data
@Audited
@Entity
@Table(name = "address")
public class Address extends BaseEntity {

    @Column(name = "street")
    private String street;

    @Column(name = "house_number")
    private String houseNumber;

    @Column(name = "apartment_number")
    private String apartmentNumber;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "full_address")
    private String fullAddress;

    @ManyToMany(mappedBy = "addresses",fetch = FetchType.EAGER)
    private Collection<UserEntity> userEntities;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estate_id", referencedColumnName = "estate_id")
    private Estate estate;
}
