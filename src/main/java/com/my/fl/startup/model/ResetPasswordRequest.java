package com.my.fl.startup.model;

import lombok.Data;

@Data
public class ResetPasswordRequest {

    private String emailOrPhone;
    private String newPassword;
    private String otp;



}
