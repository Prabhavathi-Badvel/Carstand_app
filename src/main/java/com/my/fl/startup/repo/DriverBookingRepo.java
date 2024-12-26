package com.my.fl.startup.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.my.fl.startup.entity.DriverBooking;
import com.my.fl.startup.entity.enums.BookingStatus;

public interface DriverBookingRepo extends JpaRepository<DriverBooking, Integer> {

	List<DriverBooking> findByBookingStatus(BookingStatus string);

	DriverBooking findBySeqDriverBookingIdAndTravellerId(Integer seqDriverBookingId, String travellerId);

	@Query("SELECT db FROM DriverBooking db WHERE " + "(:fromCity IS NULL OR db.fromCity = :fromCity) AND "
			+ "(:toCity IS NULL OR db.toCity = :toCity) AND "
			+ "(:fromBookingDate IS NULL OR db.bookingDate >= :fromBookingDate) AND "
			+ "(:toBookingDate IS NULL OR db.bookingDate <= :toBookingDate) AND "
			+ "(:fromPickupDate IS NULL OR db.pickUpDate >= :fromPickupDate) AND "
			+ "(:toPickupDate IS NULL OR db.pickUpDate <= :toPickupDate)")
	List<DriverBooking> findAllDriverBookings(String fromCity, String toCity, LocalDate fromBookingDate, LocalDate toBookingDate,
			LocalDate fromPickupDate, LocalDate toPickupDate);

	List<DriverBooking> findAllByTravellerId(String travellerId);
}
