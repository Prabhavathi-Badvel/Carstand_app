package com.my.fl.startup.repo;

import com.my.fl.startup.entity.AdminSecurityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminSecurityRepository extends JpaRepository<AdminSecurityEntity,Integer> {


    Optional<AdminSecurityEntity> findByAwsAccessKey(String accessKey);
}
