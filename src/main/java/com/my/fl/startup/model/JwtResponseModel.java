package com.my.fl.startup.model;

import lombok.Data;

@Data
public class JwtResponseModel {
    private String token;
    private String username;
    private String id;
    private String email;
}
