package com.my.fl.startup.utility;
import com.my.fl.startup.entity.AdminEmailSettingsEntity;
import com.my.fl.startup.entity.RegistrationEntity;
import com.my.fl.startup.repo.AdminEmailSettingsRepo;
import com.my.fl.startup.repo.AdminSMSSettings;
import com.my.fl.startup.repo.RegistrationRepository;
import com.my.fl.startup.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityUtils {

    @Autowired
    AdminEmailSettingsRepo adminEmailSettingsRepo;

    @Autowired
    RegistrationRepository registrationRepository;


    @Autowired
    EmailService emailService;

    public static String getLoggedInUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userDetails.getUsername(); // Assuming username is the email
        }
        return null;
    }

    public String getLoggedInUserPhoneNumber() {
        String email = getLoggedInUserEmail();
        RegistrationEntity registration = registrationRepository.findByEmail(email);
        return registration.getMobileNumber(); // Assuming Registration entity has a getPhone() method
    }

    public void sendEmailTemplate(String email, Long settingsId) {
        // Fetch email settings from the repository using the provided ID
        AdminEmailSettingsEntity emailDetails = adminEmailSettingsRepo.findById(settingsId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email settings ID"));

        // Retrieve email subject and body
        String emailBody = emailDetails.getEmailBody();
        String emailSubject = emailDetails.getEmailSubject();

        // Send the email
        emailService.sendEmailMessage(email, emailBody, emailSubject);
    }

}