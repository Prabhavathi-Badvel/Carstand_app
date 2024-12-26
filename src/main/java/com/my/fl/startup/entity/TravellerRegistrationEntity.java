package com.my.fl.startup.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "traveller_registration")
public class TravellerRegistrationEntity implements Serializable {

    @Id
    @Column(name = "Traveller_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long travellerId;

    @Column(name = "Traveller_Name", length = 45)
    private String travellerName;

    @Column(name = "Traveller_Mobile", length = 45, unique = true)
    private String travellerMobile;

    @Column(name = "Traveller_Email", length = 45, unique = true)
    private String travellerEmail;

    @Column(name = "Traveller_Mobile_Otp", length = 45)
    private String travellerMobileOtp;

    @Column(name = "Traveller_Email_Otp", length = 45)
    private String travellerEmailOtp;

    @Column(name = "Traveller_Mobile_verified", length = 45)
    private String travellerMobileVerified;

    @Column(name = "Traveller_Email_Verified", length = 45)
    private String travellerEmailVerified;

    @Column(name = "Status", length = 45)
    private String status;

    @Column(name = "Travelled_password", length = 255)
    private String travelledPassword;

    @Column(name = "Registration_date")
    private LocalDate registrationDate;

    @Column(name="User_Type",length = 45)
    private String userType;
}
