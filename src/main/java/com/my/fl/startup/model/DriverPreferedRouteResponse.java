package com.my.fl.startup.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverPreferedRouteResponse {

	private Long id;

	private String fromCity;

	private String toCity;

	private LocalDate availableDate;

	private String availableTime;

	private String routeStatus;

	private String driverEmail;

	private String driverId;

	private LocalDateTime routeAddedDate;

	private String driverRouteId;

	private AddDriverModel driverModel;

}
