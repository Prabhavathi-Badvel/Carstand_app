package com.my.fl.startup.service;

import com.my.fl.startup.entity.AdminSMSEntity;
import com.my.fl.startup.model.AdminSMSSettingsModel;
import com.my.fl.startup.repo.AdminSMSSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminSMSSettingsService {

    @Autowired
    private AdminSMSSettings adminSMSSettings;

    // Save SMS Details
    public AdminSMSEntity saveSMSDetails(AdminSMSEntity smsMaintainEntity) {
        return adminSMSSettings.save(smsMaintainEntity);
    }

    // Get SMS Details by ID
    public AdminSMSEntity getSMSDetails(Long id) {
        return adminSMSSettings.findById(id).orElse(null);
    }

    // Get All SMS Details
    public List<AdminSMSEntity> getAllSMSDetails() {
        return adminSMSSettings.findAll();
    }

    // Delete SMS Details by ID

    public void deleteSMSDetails(Long id) {
        adminSMSSettings.deleteById(id);
    }
}
