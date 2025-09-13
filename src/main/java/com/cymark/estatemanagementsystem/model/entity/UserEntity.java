package com.cymark.estatemanagementsystem.model.entity;

import com.cymark.estatemanagementsystem.model.enums.Designation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;



@Getter
@Setter
@Audited
@Entity
@Table(name = "USER_ENTITY")
public class UserEntity extends User {

//    @Column(name = "username", unique = true, nullable = false)
//    private String username;

    @Column(name = "profile_image")
    private String profileImage;

//    @Column(name = "user_id", unique = true, nullable = false)
//    private String userId;

    @Column(name = "pin")
    private String pin;

    @Column(name = "pin_attempts")
    protected Integer pinAttempts = 0;

    @Column(name = "country")
    private String country;

    @Column(name = "referred_by")
    private String referredBy;

    @Column(name = "short_bio")
    private String shortBio;

    @Column(name = "save_card")
    private boolean saveCard;

    @Column(name = "enable_push")
    private boolean enablePush;

    @Column(name = "is_occupancy_verified", nullable = false)
    private boolean isOccupancyVerified = false;

    @Column(name = "designation", nullable = false)
    @Enumerated(EnumType.STRING)
    private Designation designation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

}
