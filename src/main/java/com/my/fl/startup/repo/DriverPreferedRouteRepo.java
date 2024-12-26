package com.my.fl.startup.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.my.fl.startup.entity.DriverPreferedRoute;

@Repository
public interface DriverPreferedRouteRepo extends JpaRepository<DriverPreferedRoute, Long> {

	@Query(value = "Select * from driver_prefered_routes where FROM_CITY =:fromCity and TO_CITY =:toCity and DRIVER_ID =:driverId", nativeQuery = true)
	List<DriverPreferedRoute> findByFromTODriverId(@Param("fromCity") String fromCity, @Param("toCity") String toCity,
			@Param("driverId") String driverId);

	DriverPreferedRoute findByDriverIdAndId(String driverId, Long id);

	@Query(value = "SELECT * FROM driver_prefered_routes WHERE FROM_CITY = :fromCity AND TO_CITY = :toCity AND DRIVER_ID IN (:driverIds)", nativeQuery = true)
	List<DriverPreferedRoute> findByFromCityAndToCityAndDriverIds(@Param("fromCity") String fromCity,
			@Param("toCity") String toCity, @Param("driverIds") List<String> driverIds);

	@Query("SELECT d FROM DriverPreferedRoute d  WHERE (:fromLocation IS NULL OR d.fromCity = :fromLocation) "
			+ "AND (:toLocation IS NULL OR d.toCity = :toLocation) "
			+ "AND (:pickUpDate IS NULL OR d.availableDate = :pickUpDate)")
	Page<DriverPreferedRoute> findAllDriverPreferedRoutes(@Param("fromLocation") String fromLocation,
			@Param("toLocation") String toLocation, @Param("pickUpDate") LocalDate pickUpDate, Pageable pageable);

}
