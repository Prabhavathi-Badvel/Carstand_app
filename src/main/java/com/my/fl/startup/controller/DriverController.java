package com.my.fl.startup.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.my.fl.startup.entity.DriverService;
import com.my.fl.startup.entity.enums.BookingStatus;
import com.my.fl.startup.model.DriverBookingModel;
import com.my.fl.startup.model.DriverBookingResponse;
import com.my.fl.startup.utility.PaginatedResponse;

@RestController
@RequestMapping("/api/driver/")
public class DriverController {

	@Autowired
	DriverService driverService;

	@GetMapping("get-driver-bookings")
	public PaginatedResponse<DriverBookingModel> getBookings(
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "pageNo", required = false) Integer pageNo,
			@RequestParam(value = "travellerId", required = false) String travellerId,
			@RequestParam(value = "seqDriverBookingId", required = false) Integer seqDriverBookingId,
			@RequestParam(value = "bookingStatus", required = false) BookingStatus bookingStatus,
			@RequestParam(value = "fromLocation", required = false) String fromLocation,
			@RequestParam(value = "toLocation", required = false) String toLocation,
			@RequestParam(value = "fromDate", required = false) LocalDate fromDate,
			@RequestParam(value = "toDate", required = false) LocalDate toDate,
			@RequestParam(value = "pickUpDate", required = false) LocalDate pickUpDate,
			@RequestParam(value = "phoneNumber", required = false) String phoneNumber) {
		Integer parsePageSize = pageSize != null ? pageSize : 10;
		Integer parsePageNo = pageNo != null ? pageNo : 0;
		return driverService.getBookings(parsePageNo, parsePageSize, travellerId, seqDriverBookingId, bookingStatus,
				fromLocation, toLocation, fromDate, toDate, pickUpDate, phoneNumber);
	}

	@GetMapping("confirmation-bookings")
	public ResponseEntity<?> getConfirmationBookings() { // same param like searchBookingByDriver
		return driverService.getConfirmationBookings();
	}

	@GetMapping("cancelation-bookings")
	public ResponseEntity<?> getCancelationBookings() { // same param like searchBookingByDriver
		return driverService.getCancelationBookings();
	}

	@GetMapping("search-bookings")
	public ResponseEntity<?> searchDriver(@RequestParam(name = "city") String city) {
		return driverService.searchDriver(city);
	}

	@GetMapping("by-search-bookings")
	public ResponseEntity<List<DriverBookingResponse>> searchBookingByDriver(
			@RequestParam(required = false) String fromCity, @RequestParam(required = false) String toCity, // driver_booking
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fromBookingDate,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate toBookingDate, // driver_booking table
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fromPickupDate,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate toPickupDate, // driver_booking
			@RequestParam(required = false) String travellerPhone) {
		return ResponseEntity.ok(driverService.searchBookingByDriver(fromCity, toCity, fromBookingDate, toBookingDate,
				fromPickupDate, toPickupDate, travellerPhone));
	}

	@PatchMapping("change-driver-booking-status")
	public Object changeDriverBookingStatus(@RequestParam(value = "travellerId") String travellerId,
			@RequestParam(value = "seqDriverBookingId") Integer seqDriverBookingId,
			@RequestParam(value = "bookingStatus") BookingStatus bookingStatus) {
		return driverService.changeDriverBookingStatus(travellerId, seqDriverBookingId, bookingStatus);
	}

}
