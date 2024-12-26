package com.my.fl.startup.controller;

import com.my.fl.startup.entity.AdminUIResourceEntity;
import com.my.fl.startup.service.AdminUIResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/UI")
public class AdminUIResourceController {

    @Autowired
    private AdminUIResourceService adminUIResourceService;

    @GetMapping
    public List<AdminUIResourceEntity> getAllEntities() {
        return adminUIResourceService.getAllEntities();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminUIResourceEntity> getEntityById(@PathVariable Long id) {
        Optional<AdminUIResourceEntity> entity = adminUIResourceService.getEntityById(id);
        return entity.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public AdminUIResourceEntity createEntity(@RequestBody AdminUIResourceEntity entity) {
        return adminUIResourceService.createEntity(entity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminUIResourceEntity> updateEntity(@PathVariable Long id, @RequestBody AdminUIResourceEntity entityDetails) {
        AdminUIResourceEntity updatedEntity = adminUIResourceService.updateEntity(id, entityDetails);
        if (updatedEntity != null) {
            return ResponseEntity.ok(updatedEntity);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntity(@PathVariable Long id) {
        adminUIResourceService.deleteEntity(id);
        return ResponseEntity.noContent().build();
    }
}
