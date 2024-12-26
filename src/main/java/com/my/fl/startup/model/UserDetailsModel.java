package com.my.fl.startup.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailsModel {

    private String businessId;
    private String userId;
    private String name;
    private String username;
    private String email;
    private String password;
    private String mobileNumber;
    private String roleName;
    private Boolean isVerified;
    private Boolean isArchived;
    private Boolean isAvailable;
    
    private String businessRoleId;
    private String businessUserRoleMapId;
    private String businessName;
    private boolean sync;

    ResponseModel responseModel;
}
