package com.my.fl.startup.model;

import jakarta.persistence.*;


public class AdminSMSSettingsModel {

    private Long id;

    private String sms_message;
    private String ApiKey;
    private String sender;
    private String url;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSms_message() {
        return sms_message;
    }

    public void setSms_message(String sms_message) {
        this.sms_message = sms_message;
    }

    public String getApiKey() {
        return ApiKey;
    }

    public void setApiKey(String apiKey) {
        this.ApiKey = apiKey;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
