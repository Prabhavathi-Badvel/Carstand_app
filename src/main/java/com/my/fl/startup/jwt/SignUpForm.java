package com.my.fl.startup.jwt;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpForm {

    private String name;
    private String email;
  //  private String username;
    private String password;
    private String mobileNumber;
    private String address;
    private String businessName;
    private String userType;

    private Set<String> role;

}
