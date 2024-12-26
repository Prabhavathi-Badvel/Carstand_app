package com.my.fl.startup.repo;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.my.fl.startup.entity.AddBike;
import com.my.fl.startup.entity.enums.Status;

@Repository
public interface BikeRepo extends JpaRepository<AddBike, Long> {

        @Query("SELECT b FROM AddBike b WHERE b.userId = :userId AND b.brand = :brand AND b.model = :model AND b.status = :status AND b.color = :color")
        List<AddBike> findBikesByCandidateAndFilters(
                        @Param("userId") String userId,
                        @Param("brand") String brand,
                        @Param("model") String model,
                        @Param("status") Status status,
                        @Param("color") String color);

        @Query("SELECT b FROM AddBike b WHERE " +
                        "(:model IS NULL OR b.model = :model) AND " +
                        "(:brand IS NULL OR b.brand = :brand) AND " +
                        "(:userId IS NULL OR b.userId = :userId) AND " +
                        "(:bodyType IS NULL OR b.bodyType = :bodyType) AND " +
                        "((:startDate IS NULL AND :endDate IS NULL) OR " +
                        "(b.bikeAddedDate BETWEEN :startDate AND :endDate) OR " +
                        "(:startDate IS NULL AND b.bikeAddedDate <= :endDate) OR " +
                        "(:endDate IS NULL AND b.bikeAddedDate >= :startDate))")
        List<AddBike> findBikesByFilters(
                        @Param("model") String model,
                        @Param("brand") String brand,
                        @Param("bodyType") String bodyType,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("userId") String userId);

        @Query(value = "SELECT * FROM add_bike WHERE BIKE_OWNER_ID = :userId", nativeQuery = true)
        List<AddBike> findByUserId(@Param("userId") String userId);

        @Query("SELECT b FROM AddBike b WHERE b.bikeGenId = :bikeGenId AND b.userId = :userId")
        AddBike findByBikeGenIdAndUserId(@Param("bikeGenId") String bikeGenId, @Param("userId") String userId);

        @Query("SELECT b FROM AddBike b WHERE b.bikeRegNo = :bikeRegNo")
        AddBike findByBikeRegNo(@Param("bikeRegNo") String bikeRegNo);

        @Query(value = "SELECT * FROM add_bike WHERE BIKE_GEN_ID = :bikeGenId", nativeQuery = true)
        List<AddBike> findAllByBikeGenId(@Param("bikeGenId") String bikeGenId);

        Page<AddBike> findByBikeRegNoInAndModelAndStatus(
                        List<String> availableBikeRegIds,
                        String model,
                        Status status,
                        Pageable pageable);

        Page<AddBike> findByBikeRegNoIn(List<String> availableBikeRegIds, Pageable pageable);

        AddBike findByBikeGenId(String bikeGenId);

        List<AddBike> findByBrand(String brand);

        AddBike findByBikeRegNoAndUserId(String bikeId, String userId);
}
