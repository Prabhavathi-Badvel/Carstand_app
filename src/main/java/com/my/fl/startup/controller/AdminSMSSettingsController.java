package com.my.fl.startup.controller;

import com.my.fl.startup.entity.AdminSMSEntity;
import com.my.fl.startup.model.AdminSMSSettingsModel;
import com.my.fl.startup.service.AdminSMSSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin_sms")
public class AdminSMSSettingsController {

    @Autowired
    private AdminSMSSettingsService smsMaintainService;

    @PostMapping("/save")
    public ResponseEntity<AdminSMSEntity> saveSMSDetails(@RequestBody AdminSMSEntity smsDetails) {
        AdminSMSEntity savedSMS = smsMaintainService.saveSMSDetails(smsDetails);
        return ResponseEntity.ok(savedSMS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminSMSEntity> getSMSDetails(@PathVariable Long id) {
        AdminSMSEntity smsDetails = smsMaintainService.getSMSDetails(id);
        return smsDetails != null ? ResponseEntity.ok(smsDetails) : ResponseEntity.notFound().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<AdminSMSEntity>> getAllSMSDetails() {
        List<AdminSMSEntity> smsDetailsList = smsMaintainService.getAllSMSDetails();
        return ResponseEntity.ok(smsDetailsList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSMSDetails(@PathVariable Long id) {
        smsMaintainService.deleteSMSDetails(id);
        return ResponseEntity.noContent().build();
    }
}
