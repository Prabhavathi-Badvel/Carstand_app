package com.my.fl.startup.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "driver_prefered_routes")
@Getter
@Setter
@ToString
public class DriverPreferedRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DR_PR_SEQ_ID")
    private Long id;

    @Column(name = "FROM_CITY")
    private String fromCity;

    @Column(name = "TO_CITY")
    private String toCity;

    @Column(name = "AVL_DATE")
    private LocalDate availableDate;

    @Column(name = "AVL_TIME")
    private String availableTime;

    @Column(name = "ROUTE_STATUS")
    private String routeStatus;

    @Column(name = "DRIVER_EMAIL")
    private String driverEmail;

    @Column(name = "DRIVER_ID")
    private String driverId;

    @Column(name = "ROUTE_ADDED_DATE")
    private LocalDateTime routeAddedDate;

    @Column(name = "DR_ROUTE_ID")
    private String driverRouteId;

}
