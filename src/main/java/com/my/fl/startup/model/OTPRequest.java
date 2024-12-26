package com.my.fl.startup.model;

public class OTPRequest {

    private String email;
    private String phoneNumber;
    private boolean isForgetPassword;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getForgetPassword() {
        return isForgetPassword;
    }

    public void setForgetPassword(Boolean forgetPassword) {
        isForgetPassword = forgetPassword;
    }
}
