package com.my.fl.startup.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "admin_login")
public class AdminLogin implements UserDetails {
    @Id
    private String empId;
    private String empName;
    private String empEmail;
    
    private String empMobile;
    private String empPwd;
    private Date regDate;

//    @Enumerated(EnumType.STRING)
    private String userType;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + getUserType()));
    }

    @Override
    public String getPassword() {
        return this.empPwd;
    }

    @Override
    public String getUsername() {
        return this.empEmail;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}