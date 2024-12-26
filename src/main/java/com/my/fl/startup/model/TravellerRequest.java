package com.my.fl.startup.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TravellerRequest {

    private String travellerName;

    private String travellerMobileNumber;

    private String travellerEmail;
    private String travellerMobileOtp;
    private String travellerEmailOtp;
    private String travellerMobileVerified;
    private String travellerEmailVerified;
    private String status;
    private String travelledPassword;
    private String registrationDate;

}

