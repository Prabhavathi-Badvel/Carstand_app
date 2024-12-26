package com.my.fl.startup.entity;

import java.time.LocalDate;

import com.my.fl.startup.entity.enums.CabRouteStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cab_route")
@Getter
@Setter
public class CabRouteEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CAB_ROUTE_ID")
	private Long cabRouteId;

	@Column(name = "CAB_MODEL")
	private String cabModel;

	@Column(name = "SOURCE_ADDRESS")
	private String sourceAddress;

	@Column(name = "DESTINATION")
	private String destination;

	@Column(name = "PICK_UP_DATE")
	private LocalDate pickUpDate;

	@Column(name = "is_everyday")
	private boolean isEveryDay;

	@Column(name = "FROM_LOCATION")
	private String fromLocation;

	@Column(name = "NO_OF_PASSENGER")
	private String noOfPassenger;

	@Column(name = "PRICE")
	private String price;

	@Column(name = "PRICE_PER_KM")
	private String pricePerKm;

	@Column(name = "CAB_ID")
	private String cabId;

	@Column(name = "CAB_OWNER_ID")
	private String cabOwnerId;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private CabRouteStatus status;

	@Column(name = "AVAILABILITY")
	private String availability;

	@Column(name = "ROUTE_GEN_ID")
	private String routeGenId;

	@Column(name = "ROUTE_ADDED_TIME")
	private String routeAddedTime;

	@Column(name = "SERVICE_TYPE")
	private String serviceType;

	@Column(name = "ROUTE_ADDED_DATE")
	private String routeAddedDate;

}
