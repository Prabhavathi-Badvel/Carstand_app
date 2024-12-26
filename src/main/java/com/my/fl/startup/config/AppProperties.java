package com.my.fl.startup.config;
import com.my.fl.startup.entity.AdminEmailSettingsEntity;
import com.my.fl.startup.entity.AdminSMSEntity;
import com.my.fl.startup.repo.AdminEmailSettingsRepo;
import com.my.fl.startup.repo.AdminSMSSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class AppProperties {

    private AdminEmailSettingsEntity emailConfig;
    private AdminSMSEntity smsConfig;

    private final AdminEmailSettingsRepo adminEmailSettingsRepo;
    private final AdminSMSSettings adminSMSSettings;

    @Autowired
    public AppProperties(AdminEmailSettingsRepo adminEmailSettingsRepo, AdminSMSSettings adminSMSSettings) {
        this.adminEmailSettingsRepo = adminEmailSettingsRepo;
        this.adminSMSSettings = adminSMSSettings;
    }

    // Lazy load and cache emailConfig
    @Cacheable("emailConfigCache")
    private AdminEmailSettingsEntity loadEmailConfig() {
        return adminEmailSettingsRepo.findById(1L)
                .orElseThrow(() -> new RuntimeException("No email configuration found in the database"));
    }

    // Lazy load and cache smsConfig
    @Cacheable("smsConfigCache")
    private AdminSMSEntity loadSmsConfig() {
        return adminSMSSettings.findById(1L)
                .orElseThrow(() -> new RuntimeException("No SMS configuration found in the database"));
    }

    // Getters with lazy loading and caching

    public String getMailHost() {
        if (emailConfig == null) {
            emailConfig = loadEmailConfig();
        }
        return emailConfig.getHost();
    }

    public int getMailPort() {
        if (emailConfig == null) {
            emailConfig = loadEmailConfig();
        }
        return emailConfig.getPort();
    }

    public String getMailUsername() {
        if (emailConfig == null) {
            emailConfig = loadEmailConfig();
        }
        return emailConfig.getUsername();
    }

    public String getMailPassword() {
        if (emailConfig == null) {
            emailConfig = loadEmailConfig();
        }
        try {
            return EncryptCredentials.decrypt(emailConfig.getPassword()); // Decrypt the encrypted password before use
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting the password", e);
        }
    }

    public boolean isMailSmtpAuth() {
        return true; // Assuming this value is constant
    }

    public boolean isMailSmtpStartTlsEnable() {
        return true; // Assuming this value is constant
    }

    public String getStaticLocations() {
        return "/static/"; // Assuming this value is constant or you can configure it elsewhere
    }

    public String getApiKey() {
        if (smsConfig == null) {
            smsConfig = loadSmsConfig();
        }
        return smsConfig.getApiKey();
    }

    public String getSender() {
        if (smsConfig == null) {
            smsConfig = loadSmsConfig();
        }
        return smsConfig.getSender();
    }

    public String getUrl() {
        if (smsConfig == null) {
            smsConfig = loadSmsConfig();
        }
        return smsConfig.getUrl();
    }

    public String getSmsMessage() {
        if (smsConfig == null) {
            smsConfig = loadSmsConfig();
        }
        return smsConfig.getSmsMessage();
    }
}
