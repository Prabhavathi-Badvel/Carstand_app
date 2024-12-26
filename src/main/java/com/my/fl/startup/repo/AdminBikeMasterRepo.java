package com.my.fl.startup.repo;

import com.my.fl.startup.entity.BikeMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AdminBikeMasterRepo extends JpaRepository<BikeMasterEntity, String> {

    @Query("SELECT b FROM BikeMasterEntity b WHERE (:brand IS NULL OR b.brand = :brand) " +
            "AND (:model IS NULL OR b.model = :model) " +
            "AND (:bodyType IS NULL OR b.bodyType = :bodyType)")
    List<BikeMasterEntity> findBikesByFilters(String brand, String model, String bodyType);

    @Query("SELECT b FROM BikeMasterEntity b WHERE b.brand IN :brands")
    List<BikeMasterEntity> findByBrands(List<String> brands);
}
