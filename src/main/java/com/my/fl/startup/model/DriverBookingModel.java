package com.my.fl.startup.model;

import com.my.fl.startup.entity.enums.BookingStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Getter
@Setter
public class DriverBookingModel {

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
	private String travellerId;

}
