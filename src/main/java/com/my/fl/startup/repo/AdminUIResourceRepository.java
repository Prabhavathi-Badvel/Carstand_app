package com.my.fl.startup.repo;

import com.my.fl.startup.entity.AdminUIResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminUIResourceRepository extends JpaRepository<AdminUIResourceEntity, Long> {
}