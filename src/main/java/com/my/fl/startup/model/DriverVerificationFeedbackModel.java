package com.my.fl.startup.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverVerificationFeedbackModel {

    private Long id;

    private String driverEmail;
    private String feedback;
    private String adminEmail;
    private String driverId;
    private String feedbackDate;

}
