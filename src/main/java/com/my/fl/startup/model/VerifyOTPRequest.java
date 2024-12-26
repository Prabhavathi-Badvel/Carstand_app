package com.my.fl.startup.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class VerifyOTPRequest {

    @NonNull
    private String otp;
    private String email;
    private String phoneNumber;
    private Boolean isForgetPassword;
    private Boolean isVerifyTraveller;

    private String message;
}
