package com.my.fl.startup.repo;

import com.my.fl.startup.entity.AdminSMSEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@EnableJpaRepositories
    @Repository
    public interface AdminSMSSettings extends JpaRepository<AdminSMSEntity, Long> {
    Optional<AdminSMSEntity> findTopByOrderByIdDesc();

    Optional<AdminSMSEntity> findFirstByOrderByIdAsc();
    // Additional query methods (if needed) can be defined here
    }

