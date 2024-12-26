package com.my.fl.startup.repo;

import com.my.fl.startup.entity.CabMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminCabMasterRepo extends JpaRepository<CabMasterEntity, String> {

    CabMasterEntity findByBrand(String brand);
}


