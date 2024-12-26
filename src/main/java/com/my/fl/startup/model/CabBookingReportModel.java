package com.my.fl.startup.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CabBookingReportModel {
	
	private Long cabBookingSeqNo;
    private String userEmail;
    private String contactNo;
    private String pickUpAddress;
    private String bookingId;
    private String bookingDate;
    private String bookingStatus;
    private String cabId;
    private String cabOwnerId;
    private String droppingAddress;
    private String cabRouteId;
    private String returnedDate;
    private String cabServiceType;
    private String travelingDate;
    private String pickUpTime;
    private String fromCity;
    private String toCity;

}
