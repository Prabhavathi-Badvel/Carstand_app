package com.my.fl.startup.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.my.fl.startup.entity.DriverVerificationFeedback;

@Repository
public interface DriverVerificationFeedbackRepo extends JpaRepository<DriverVerificationFeedback, Long> {
	
	@Query(value = "Select * from driver_verification_feedback where driver_id =:driverId",nativeQuery = true)
	DriverVerificationFeedback findByDriverId(@Param("driverId") String driverId);


}
