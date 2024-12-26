package com.my.fl.startup.controller;

import com.my.fl.startup.entity.AdminEmailSettingsEntity;
import com.my.fl.startup.service.AdminEmailSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
    @RequestMapping("/api/admin_email")
    public class AdminEmailSettingsController {

        @Autowired
        private AdminEmailSettingsService service;

        @GetMapping
        public List<AdminEmailSettingsEntity> getAllEmailSettings() {
            return service.getAllEmailSettings();
        }

        @GetMapping("/{id}")
        public ResponseEntity<AdminEmailSettingsEntity> getEmailSettingById(@PathVariable Long id) {
            Optional<AdminEmailSettingsEntity> setting = service.getEmailSettingById(id);
            return setting.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        }

        @PostMapping
        public AdminEmailSettingsEntity createEmailSetting(@RequestBody AdminEmailSettingsEntity emailMaintenance) {
            return service.saveEmailSetting(emailMaintenance);
        }

        @PutMapping("/{id}")
        public ResponseEntity<AdminEmailSettingsEntity> updateEmailSetting(
                @PathVariable Long id, @RequestBody AdminEmailSettingsEntity emailMaintenance) {
            if (!service.getEmailSettingById(id).isPresent()) {
                return ResponseEntity.notFound().build();
            }
            emailMaintenance.setId(id);
            return ResponseEntity.ok(service.saveEmailSetting(emailMaintenance));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteEmailSetting(@PathVariable Long id) {
            if (!service.getEmailSettingById(id).isPresent()) {
                return ResponseEntity.notFound().build();
            }
            service.deleteEmailSetting(id);
            return ResponseEntity.noContent().build();
        }
    }


