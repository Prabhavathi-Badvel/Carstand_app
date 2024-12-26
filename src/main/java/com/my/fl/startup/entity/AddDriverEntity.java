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
@Table(name = "add_driver")
@Getter
@Setter
public class AddDriverEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SEQ_DRIVER_ID")
	private Long seqDriverId;

	@Column(name = "FIRST_NAME")
	private String firstName;

	@Column(name = "LAST_NAME")
	private String lastName;

	@Column(name = "EMAIL")
	private String email;

	@Column(name = "MOBILE_NO")
	private String mobileNo;

	@Column(name = "DOB")
	private String dob;

	@Column(name = "ADDRESS")
	private String address;

	@Column(name = "STREET")
	private String street;

	@Column(name = "CITY")
	private String city;

	@Column(name = "STATE")
	private String state;

	@Column(name = "DISTRICT")
	private String district;

	@Column(name = "PINCODE")
	private String pincode;

	@Column(name = "REGISTERED_STATE")
	private String registeredState;

	@Column(name = "LICENSE_NO")
	private String licenseNo;

	@Column(name = "LICENSE_TYPE")
	private String licenseType;

	@Column(name = "EXPIRY_DATE")
	private String expiryDate;

	@Column(name = "DRIVING_EXP")
	private String drivingExp;

	@Column(name = "PERMIT_TYPE")
	private String permitType;

	@Column(name = "WITHIN_RANGE")
	private String withinRange;

	@Column(name = "ADHAR_NO")
	private String adharNo;

	@Column(name = "PAN_NO")
	private String panNo;

	@Column(name = "LICENSE_DOC")
	private String licenseDoc;

	@Column(name = "PHOTO")
	private String photo;

	@Column(name = "ADHAR_DOC")
	private String adharDoc;

	@Column(name = "RC_DOC")
	private String rcDoc;

	@Column(name = "INSURANCE_DOC")
	private String insuranceDoc;

	@Column(name = "M_USER_ID")
	private String mUserId;

	@Column(name = "AVAILABILITY")
	private String availability;

	@Column(name = "DRIVER_ID")
	private String driverId;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "JOB_TYPE")
	private String jobType;

	@Column(name = "REGISTERED_DATE")
	private String registeredDate;

	@Column(name="COMMERCIAL_LICENSE")
	private String commercialLicense;

	@Column(name = "WORKED_LOCATION")
	private String workedLocation;



}