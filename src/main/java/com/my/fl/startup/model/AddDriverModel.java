package com.my.fl.startup.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddDriverModel {

	private Long seqDriverId;
	private String firstName;
	private String lastName;
	private String email;
	private String mobileNo;
	private String dob;
	private String address;
	private String street;
	private String city;
	private String state;
	private String district;
	private String pincode;
	private String registeredState;
	private String licenseNo;
	private String licenseType;
	private String expiryDate;
	private String drivingExp;
	private String permitType;
	private String withinRange;
	private String adharNo;
	private String panNo;
	private String licenseDoc;
	private String photo;
	private String insuranceDoc;
	private String adharDoc;
	private String rcDoc;
	private String mUserId;
	private String availability;
	private String driverId;
	private com.my.fl.startup.entity.enums.Status status;
	private String jobType;
	private String registeredDate;
	private String commercialLicense;
	private String workedLocation;

}
