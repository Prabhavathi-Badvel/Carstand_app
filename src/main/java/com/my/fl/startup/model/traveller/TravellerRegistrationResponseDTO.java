package com.my.fl.startup.model.traveller;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TravellerRegistrationResponseDTO implements Serializable {

    @Column(name = "Traveller_id", nullable = false)
    private String travellerId;

    @Column(name = "Traveller_Name", length = 45)
    private String travellerName;

    @Column(name = "Traveller_Mobile", length = 45, unique = true)
    private String travellerMobile;

    @Column(name = "Traveller_Email", length = 45, unique = true)
    private String travellerEmail;

    @Column(name = "Traveller_Mobile_verified", length = 45)
    private String travellerMobileVerified;

    @Column(name = "Traveller_Email_Verified", length = 45)
    private String travellerEmailVerified;

    @Column(name = "Status", length = 45)
    private String status;

    @Column(name = "Registration_date")
    private String registrationDate;

    @Column(name="User_Type",length = 45)
    private String userType;
    private String token;
}
