package com.my.fl.startup.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.my.fl.startup.entity.CabBooking;

@Repository
public interface CabBookingRepo extends JpaRepository<CabBooking, Long> {

	@Query(value = "Select * from cab_booking where CAB_ID =:cabId", nativeQuery = true)
	List<CabBooking> findByCabId(@Param("cabId") String cabId);

	@Query("select cb from CabBooking cb where cb.cabId=:cabId")
	Optional<CabBooking> findbyCabId(String cabId);

	@Query(value = "Select * from cab_booking where CAB_ID =:cabId and BOOKING_STATUS =:bookingStatus", nativeQuery = true)
	List<CabBooking> findByStatus(@Param("cabId") String cabId, @Param("bookingStatus") String bookingStatus);

}
