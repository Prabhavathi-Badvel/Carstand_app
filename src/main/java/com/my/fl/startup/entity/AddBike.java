package com.my.fl.startup.entity;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.my.fl.startup.entity.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "add_bike")
@Getter
@Setter
public class AddBike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "BIKE_SEQ_ID")
    private Long bikeSeqId;

    @Column(name = "BIKE_REG_NO")
    private String bikeRegNo;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "BIKE_GEN_ID")
    private String bikeGenId;

    @Column(name = "VEHICLE_ID_NO")
    private String vehicleIdNo;

    @Column(name = "RC_DOC")
    private String rcDoc;

    @Column(name = "INSURANCE_DOC")
    private String insuranceDoc;

    @Column(name = "BIKE_PHOTO")
    private String bikePhoto;

    @Column(name = "BIKE_BRAND")
    private String brand;

    @Column(name = "BIKE_MODEL")
    private String model;

    @Column(name = "MODEL_YEAR")
    private String modelYear;

    @Column(name = "CURRENT_MILEAGE")
    private String currentMileage;

    @Column(name = "FUEL_TYPE")
    private String fuelType;

    @Column(name = "BODY_TYPE")
    private String bodyType;

    @Column(name = "TRANSMISSION")
    private String transmission;

    @Column(name = "KM_DRIVEN")
    private String kmDriven;

    @Column(name = "NO_OF_PASSENGER")
    private String numberOfPassenger;

    @Column(name = "COLOR")
    private String color;

    @Column(name = "INSURANCE_COMP_NAME")
    private String insuranceCompanyName;

    @Column(name = "CERTIFIED_COMP_NAME")
    private String certifiedCompanyName;

    @Column(name = "REGISTERED_YEAR")
    private String registeredYear;

    @Column(name = "REGISTERED_CITY")
    private String registeredCity;

    @Column(name = "REGISTERED_STATE")
    private String registeredState;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private Status status;

    @Column(name = "MOBILE_NO")
    private String mobileNo;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "CITY")
    private String city;

    @Column(name = "STATE")
    private String state;

    @Column(name = "DISTRICT")
    private String district;

    @Column(name = "PINCODE")
    private String pincode;

    @Column(name = "BIKE_ADDED_DATE")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate bikeAddedDate;

    @Column(name = "BIKE_PLATE_STATUS")
    private String bikePlateStatus;

    @Column(name = "BIKE_EXPIRY_DATE")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate bikeExpiryDate;

    @Column(name = "BIKE_VALID_DAYS")
    private Integer bikeValidDays;

    @PrePersist
    protected void onCreate() {
        this.bikeAddedDate = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.bikeAddedDate = LocalDate.now();
    }
}
