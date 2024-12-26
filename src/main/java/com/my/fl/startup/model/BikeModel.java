package com.my.fl.startup.model;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.my.fl.startup.entity.enums.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BikeModel {

    private Long bikeSeqId;
    private String bikeRegNo;
    private String userId;
    private String bikeGenId;
    private String vehicleIdNo;
    private String rcDoc;
    private String insuranceDoc;
    private String bikePhoto;
    private String brand;
    private String model;
    private String modelYear;
    private String currentMileage;
    private String fuelType;
    private String bodyType;
    private String transmission;
    private String kmDriven;
    private String numberOfPassenger;
    private String color;
    private String insuranceCompanyName;
    private String certifiedCompanyName;
    private String registeredYear;
    private String registeredCity;
    private String registeredState;
    private Status status;
    private String mobileNo;
    private String address;
    private String city;
    private String state;
    private String district;
    private String pincode;
    private Integer bikeValidDays;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate bikeAddedDate;

    private String bikePlateStatus;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate bikeExpiryDate;

    private String membershipPlanId;
}
