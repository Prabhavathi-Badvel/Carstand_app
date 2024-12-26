package com.my.fl.startup.service;

import com.my.fl.startup.entity.AdminUIResourceEntity;
import com.my.fl.startup.repo.AdminUIResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminUIResourceService {

    @Autowired
    private AdminUIResourceRepository adminUIResourceRepository;

    public List<AdminUIResourceEntity> getAllEntities() {
        return adminUIResourceRepository.findAll();
    }

    public Optional<AdminUIResourceEntity> getEntityById(Long id) {
        return adminUIResourceRepository.findById(id);
    }

    public AdminUIResourceEntity createEntity(AdminUIResourceEntity entity) {
        return adminUIResourceRepository.save(entity);
    }

    public AdminUIResourceEntity updateEntity(Long id, AdminUIResourceEntity entityDetails) {
        Optional<AdminUIResourceEntity> optionalEntity = adminUIResourceRepository.findById(id);
        if (optionalEntity.isPresent()) {
            AdminUIResourceEntity existingEntity = optionalEntity.get();
            existingEntity.setName(entityDetails.getName());
            existingEntity.setValue(entityDetails.getValue());
            return adminUIResourceRepository.save(existingEntity);
        }
        return null;
    }

    public void deleteEntity(Long id) {
        if(adminUIResourceRepository.existsById(id)) {
            adminUIResourceRepository.deleteById(id);
        }
    }
}