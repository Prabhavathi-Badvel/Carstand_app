package com.my.fl.startup.model;

import lombok.Data;

@Data
public class AdminLoginResponse {

    private String empId;
    private String empEmail;
    private String empMobileNumber;
    private String jwtToken;
}
