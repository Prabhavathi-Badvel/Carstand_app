package com.my.fl.startup.model;

import java.time.LocalDate;
import java.time.LocalTime;

import com.my.fl.startup.entity.enums.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverBookingResponse {

	private Integer seqDriverBookingId;

	private String pickUpAddress;

	private String droppingAddress;

	private LocalDate pickUpDate;

	private LocalTime pickUpTime;

	private String contactNo;

	private String userEmail;

	private String driverEmail;

	private String driverId;

	private String bookingId;

	private LocalDate bookingDate;

	private BookingStatus bookingStatus;

	private String driverRouteId;

	private String fromCity;

	private String toCity;

	private TravellerRegistrationResponse travellerRegistrationResponse;

}