package com.my.fl.startup.entity;


import jakarta.persistence.*;
@Table(name = "admin_carstand_sms")
@Entity
    public class AdminSMSEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "sms_message", nullable = false)
        private String smsMessage;

        @Column(name = "api_key", nullable = false)
        private String apiKey;

        @Column(name = "sender", nullable = false)
        private String sender;

        @Column(name = "url", nullable = false)
        private String url;

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getSmsMessage() {
            return smsMessage;
        }

        public void setSmsMessage(String smsMessage) {
            this.smsMessage = smsMessage;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
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


