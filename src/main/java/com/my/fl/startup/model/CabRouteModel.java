package com.my.fl.startup.model;

import java.time.LocalDate;

import com.my.fl.startup.entity.enums.CabRouteStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CabRouteModel {

	private Long cabRouteId;

	private String cabModel;
	private String sourceAddress;
	private String destination;
	private LocalDate pickUpDate;
	private boolean isEveryDay;
	private String fromLocation;
	private String noOfPassenger;
	private String price;
	private String pricePerKm;
	private String cabId;
	private String cabOwnerId;
	private CabRouteStatus status;
	private String availability;
	private String routeGenId;
	private String routeAddedTime;
	private String serviceType;
	private String routeAddedDate;

}
