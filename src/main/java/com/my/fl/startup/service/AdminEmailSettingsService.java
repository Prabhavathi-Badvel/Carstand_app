package com.my.fl.startup.service;

import com.my.fl.startup.entity.AdminEmailSettingsEntity;
import com.my.fl.startup.repo.AdminEmailSettingsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

    @Service
    public class AdminEmailSettingsService {

        @Autowired
        private AdminEmailSettingsRepo repository;

        public List<AdminEmailSettingsEntity> getAllEmailSettings() {
            return repository.findAll();
        }

        public Optional<AdminEmailSettingsEntity> getEmailSettingById(Long id) {
            return repository.findById(id);
        }

        public AdminEmailSettingsEntity saveEmailSetting(AdminEmailSettingsEntity emailMaintenance) {
            return repository.save(emailMaintenance);
        }

        public void deleteEmailSetting(Long id) {
            repository.deleteById(id);
        }

        // Add more methods if necessary
    }


