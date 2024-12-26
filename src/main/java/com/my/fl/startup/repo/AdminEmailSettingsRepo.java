package com.my.fl.startup.repo;

import com.my.fl.startup.entity.AdminEmailSettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@EnableJpaRepositories
@Repository
public interface AdminEmailSettingsRepo extends JpaRepository<AdminEmailSettingsEntity, Long> {
    Optional<AdminEmailSettingsEntity> findTopByOrderByIdDesc(); // Fetch the most recent configuration

    // Additional query methods can be added here if needed
}
