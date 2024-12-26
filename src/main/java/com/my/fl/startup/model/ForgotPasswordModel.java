package com.my.fl.startup.model;



public class ForgotPasswordModel {
    private String emailOrPhone;
    private String newPassword;
    private String oldPassword;
    private Boolean forgotPassword;
    private Boolean resetPassword;

    public String getEmailOrPhone() {
        return emailOrPhone;
    }

    public void setEmailOrPhone(String emailOrPhone) {
        this.emailOrPhone = emailOrPhone;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public Boolean getForgotPassword() {
        return forgotPassword;
    }

    public void setForgotPassword(Boolean forgotPassword) {
        this.forgotPassword = forgotPassword;
    }

    public Boolean getResetPpassword() {
        return resetPassword;
    }

    public void setResetPpassword(Boolean resetPpassword) {
        this.resetPassword = resetPpassword;
    }

    @Override
    public String toString() {
        return "ForgotPasswordModel{" +
                "emailOrPhone='" + emailOrPhone + '\'' +
                ", newPassword='" + newPassword + '\'' +
                ", oldPassword='" + oldPassword + '\'' +
                ", forgotPassword=" + forgotPassword +
                ", resetPpassword=" + resetPassword +
                '}';
    }
}
