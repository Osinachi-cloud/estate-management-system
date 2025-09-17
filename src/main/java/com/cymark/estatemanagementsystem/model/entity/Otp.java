package com.cymark.estatemanagementsystem.model.entity;

import com.cymark.estatemanagementsystem.model.enums.OTType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Entity
@Data
@NoArgsConstructor
@Table(name = "otp")
public class Otp extends BaseEntity {

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email_address")
    private String emailAddress;

    @Column(name = "is_verified")
    private boolean verified;

    @Column(name = "otp_code")
    private String otpCode;

    @Column(name = "generated_on")
    private Instant generatedOn;

    @Column(name = "expired_on")
    private Instant expiredOn;

    @Column(name = "otp_type")
    @Enumerated(EnumType.STRING)
    private OTType OTPType;
}