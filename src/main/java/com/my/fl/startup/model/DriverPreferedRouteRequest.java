package com.my.fl.startup.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class DriverPreferedRouteRequest {

    private Long id;
    private String fromCity;
    private String toCity;
    private String availableDate;
    private String availableTime;
    private String routeStatus;
    private String driverEmail;
    private String driverId;
    private LocalDateTime routeAddedDate;
    private String driverRouteId;

}
