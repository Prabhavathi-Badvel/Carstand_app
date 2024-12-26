package com.my.fl.startup.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cab_booking")
@Getter
@Setter
public class CabBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CAB_BOOKING_SEQ_NO")
    private Long cabBookingSeqNo;

    @Column(name = "USER_EMAIL")
    private String userEmail;

    @Column(name = "CONTACT_NO")
    private String contactNo;

    @Column(name = "PICK_UP_ADDRESS")
    private String pickUpAddress;

    @Column(name = "BOOKING_ID")
    private String bookingId;

    @Column(name = "BOOKING_DATE")
    private String bookingDate;

    @Column(name = "BOOKING_STATUS")
    private String bookingStatus;

    @Column(name = "CAB_ID")
    private String cabId;

    @Column(name = "CAB_OWNER_ID")
    private String cabOwnerId;

    @Column(name = "DROPPING_ADDRESS")
    private String droppingAddress;

    @Column(name = "CAB_ROUTE_ID")
    private String cabRouteId;

    @Column(name = "RETURNED_DATE")
    private String returnedDate;

    @Column(name = "CAB_SERVICE_TYPE")
    private String cabServiceType;

    @Column(name = "TRAVELING_DATE")
    private String travelingDate;

    @Column(name = "PICK_UP_TIME")
    private String pickUpTime;

    @Column(name = "FROM_CITY")
    private String fromCity;

    @Column(name = "TO_CITY")
    private String toCity;
}

