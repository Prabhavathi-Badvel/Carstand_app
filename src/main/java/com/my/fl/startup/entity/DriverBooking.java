package com.my.fl.startup.entity;

import com.my.fl.startup.entity.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "driver_booking")
@Getter
@Setter
public class DriverBooking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "DR_BOOKING_SEQ_ID")
	private Integer seqDriverBookingId;

	@Column(name = "PICK_UP_ADDRESS")
	private String pickUpAddress;

	@Column(name = "DROPPING_ADDRESS")
	private String droppingAddress;

	@Column(name = "PICK_UP_DATE")
	private LocalDate pickUpDate;

	@Column(name = "PICK_UP_TIME")
	private LocalTime pickUpTime;

	@Column(name = "CONTACT_NO")
	private String contactNo;

	@Column(name = "USER_EMAIL")
	private String userEmail;

	@Column(name = "DRIVER_EMAIL")
	private String driverEmail;

	@Column(name = "DRIVER_ID")
	private String driverId;

	@Column(name = "BOOKING_ID")
	private String bookingId;

	@Column(name = "BOOKING_DATE")
	private LocalDate bookingDate;

	@Column(name = "BOOKING_STATUS")
	private BookingStatus bookingStatus;

	@Column(name = "DR_ROUTE_GEN_ID")
	private String driverRouteId;

	@Column(name = "FROM_CITY")
	private String fromCity;

	@Column(name = "TO_CITY")
	private String toCity;

	@Column(name = "TRAVELLER_ID")
	private String travellerId;

}