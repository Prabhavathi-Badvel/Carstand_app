package com.my.fl.startup.entity;


import jakarta.persistence.*;

@Table(name = "admin_carstand_email")
    @Entity
    public class AdminEmailSettingsEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String host;
        private int port;
        private String emailSubject;
        private String username;
        private String password;
        private String emailBody;

        // Constructors
        public AdminEmailSettingsEntity() {}

    public String getEmailBody() {
        return emailBody;
    }

    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }

    public AdminEmailSettingsEntity(String host, int port, String emailSubject, String username, String password, String emailBody) {
            this.host = host;
            this.port = port;
            this.emailSubject = emailSubject;
            this.username = username;
            this.password = password;
            this.emailBody= emailBody;
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getEmailSubject() {
            return emailSubject;
        }

        public void setEmailSubject(String emailSubject) {
            this.emailSubject = emailSubject;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }




