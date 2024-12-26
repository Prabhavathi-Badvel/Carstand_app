package com.my.fl.startup.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.my.fl.startup.entity.AddDriverEntity;
import com.my.fl.startup.model.AddDriverResponseGet;

@Repository
public interface AddDriverRepo extends JpaRepository<AddDriverEntity, Long> {

	List<AddDriverEntity> findByCityAndAvailability(String city, String string);

	// List<AddDriverModel> findDrivers(String city, String mobileNo, String email,
	// String driverId, String startDate, String endDate);

	@Query("SELECT d FROM AddDriverEntity d WHERE (:mUserId IS NULL OR d.mUserId = :mUserId) AND"
			+ "(:city IS NULL OR d.city = :city) AND " + "(:mobileNo IS NULL OR d.mobileNo = :mobileNo) AND "
			+ "(:email IS NULL OR d.email = :email) AND " + "(:driverId IS NULL OR d.driverId = :driverId) AND "
			+ "(:startDate IS NULL OR d.registeredDate >= :startDate) AND "
			+ "(:endDate IS NULL OR d.registeredDate <= :endDate)")
	Page<AddDriverEntity> findDrivers(@Param("mUserId") String mUserId, @Param("city") String city,
			@Param("mobileNo") String mobileNo, @Param("email") String email, @Param("driverId") String driverId,
			@Param("startDate") String startDate, @Param("endDate") String endDate, Pageable pageable);

	@Query(value = "Select * From add_driver Where M_USER_ID =:mUserId and DRIVER_ID =:driverId", nativeQuery = true)
	List<AddDriverEntity> findByMUserIdDriverId(String mUserId, String driverId);

	@Query(value = "Select * From add_driver Where M_USER_ID =:mUserId", nativeQuery = true)
	List<AddDriverEntity> findByMUserId(String mUserId);

	AddDriverEntity findByDriverId(String driverId);
}
