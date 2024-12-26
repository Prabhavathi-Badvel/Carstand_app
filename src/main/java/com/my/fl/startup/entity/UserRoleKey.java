package com.my.fl.startup.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
public class UserRoleKey implements Serializable {
    private Long roleId;
    private Long regId;


}
