package com.my.fl.startup.entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.my.fl.startup.entity.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "add_cab")
@Setter
@Getter
public class AddCab {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CAB_SEQ_ID")
	private Long cabSeqId;

	@Column(name = "CAB_REG_NO")
	private String cabRegNo;

	@Column(name = "CAB_BRAND")
	private String cabBrand;

	@Column(name = "CAB_MODEL")
	private String cabModel;

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

	@Column(name = "INSURENCE_COMP_NAME")
	private String insuranceCompanyName;

	@Column(name = "CERTIFIED_COMP_NAME")
	private String certifiedCompanyName;

	@Column(name = "REGISTERED_YEAR")
	private String registeredYear;

	@Column(name = "REGISTERED_CITY")
	private String registeredCity;

	@Column(name = "REGISTERED_STATE")
	private String registeredState;

	@Column(name = "RC_DOC")
	private String rcDoc;

	@Column(name = "INSURANCE_DOC")
	private String insuranceDoc;

	@Column(name = "CAB_PHOTO")
	private String cabPhoto;

	@Column(name = "CAB_OWNER_ID")
	private String cabOwnerId;

	@Column(name = "CAB_GEN_ID")
	private String cabGenId;

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

	@Column(name = "CAB_ADDED_DATE")
	private LocalDate cabAddedDate;

	@Column(name = "CAB_PLATE_STATUS")
	private String cabPlateStatus;

	@Column(name = "CAB_EXPIRY_DATE")
	private String cabExpiryDate;

	@Column(name = "CAB_VALID_DAYS")
	private String cabValidDays;

//	public Long getCabSeqId() {
//		return cabSeqId;
//	}
//
//	public String getCabRegNo() {
//		return cabRegNo;
//	}
//
//	public String getCabBrand() {
//		return cabBrand;
//	}
//
//	public String getCabModel() {
//		return cabModel;
//	}
//
//	public String getModelYear() {
//		return modelYear;
//	}
//
//	public String getCurrentMileage() {
//		return currentMileage;
//	}
//
//	public String getFuelType() {
//		return fuelType;
//	}
//
//	public String getBodyType() {
//		return bodyType;
//	}
//
//	public String getTransmission() {
//		return transmission;
//	}
//
//	public String getKmDriven() {
//		return kmDriven;
//	}
//
//	public String getNumberOfPassenger() {
//		return numberOfPassenger;
//	}
//
//	public String getColor() {
//		return color;
//	}
//
//	public String getInsuranceCompanyName() {
//		return insuranceCompanyName;
//	}
//
//	public String getCertifiedCompanyName() {
//		return certifiedCompanyName;
//	}
//
//	public String getRegisteredYear() {
//		return registeredYear;
//	}
//
//	public String getRegisteredCity() {
//		return registeredCity;
//	}
//
//	public String getRegisteredState() {
//		return registeredState;
//	}
//
//	public String getRcDoc() {
//		return rcDoc;
//	}
//
//	public String getInsuranceDoc() {
//		return insuranceDoc;
//	}
//
//	public String getCabPhoto() {
//		return cabPhoto;
//	}
//
//	public String getCabOwnerId() {
//		return cabOwnerId;
//	}
//
//	public String getCabGenId() {
//		return cabGenId;
//	}
//
//	public Status getStatus() {
//		return status;
//	}
//
//	public String getMobileNo() {
//		return mobileNo;
//	}
//
//	public String getAddress() {
//		return address;
//	}
//
//	public String getCity() {
//		return city;
//	}
//
//	public String getState() {
//		return state;
//	}
//
//	public String getDistrict() {
//		return district;
//	}
//
//	public String getPincode() {
//		return pincode;
//	}
//
//	public String getCabAddedDate() {
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//		return cabAddedDate.format(formatter);
//	}
//
//	public String getCabPlateStatus() {
//		return cabPlateStatus;
//	}
//
//	public String getCabExpiryDate() {
//		return cabExpiryDate;
//	}
//
//	public String getCabValidDays() {
//		return cabValidDays;
//	}

}
