package com.my.fl.startup.model;

import java.time.LocalDate;

import com.my.fl.startup.entity.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class CabModel {

    private Long cabSeqId;
    private String cabRegNo;
    private String cabBrand;
    private String cabModel;
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
    private String rcDoc;
    private String insuranceDoc;
    private String cabPhoto;
    private String cabOwnerId;
    private String cabGenId;
    private Status status;
    private String mobileNo;
    private String address;
    private String city;
    private String state;
    private String district;
    private String pincode;
    private LocalDate cabAddedDate;
    private String cabPlateStatus;
    private String cabExpiryDate;
    private int cabValidDays;
    private Long memberShipPlanId;
	public Long getCabSeqId() {
		return cabSeqId;
	}
	public void setCabSeqId(Long cabSeqId) {
		this.cabSeqId = cabSeqId;
	}
	public String getCabRegNo() {
		return cabRegNo;
	}
	public void setCabRegNo(String cabRegNo) {
		this.cabRegNo = cabRegNo;
	}
	public String getCabBrand() {
		return cabBrand;
	}
	public void setCabBrand(String cabBrand) {
		this.cabBrand = cabBrand;
	}
	public String getCabModel() {
		return cabModel;
	}
	public void setCabModel(String cabModel) {
		this.cabModel = cabModel;
	}
	public String getModelYear() {
		return modelYear;
	}
	public void setModelYear(String modelYear) {
		this.modelYear = modelYear;
	}
	public String getCurrentMileage() {
		return currentMileage;
	}
	public void setCurrentMileage(String currentMileage) {
		this.currentMileage = currentMileage;
	}
	public String getFuelType() {
		return fuelType;
	}
	public void setFuelType(String fuelType) {
		this.fuelType = fuelType;
	}
	public String getBodyType() {
		return bodyType;
	}
	public void setBodyType(String bodyType) {
		this.bodyType = bodyType;
	}
	public String getTransmission() {
		return transmission;
	}
	public void setTransmission(String transmission) {
		this.transmission = transmission;
	}
	public String getKmDriven() {
		return kmDriven;
	}
	public void setKmDriven(String kmDriven) {
		this.kmDriven = kmDriven;
	}
	public String getNumberOfPassenger() {
		return numberOfPassenger;
	}
	public void setNumberOfPassenger(String numberOfPassenger) {
		this.numberOfPassenger = numberOfPassenger;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getInsuranceCompanyName() {
		return insuranceCompanyName;
	}
	public void setInsuranceCompanyName(String insuranceCompanyName) {
		this.insuranceCompanyName = insuranceCompanyName;
	}
	public String getCertifiedCompanyName() {
		return certifiedCompanyName;
	}
	public void setCertifiedCompanyName(String certifiedCompanyName) {
		this.certifiedCompanyName = certifiedCompanyName;
	}
	public String getRegisteredYear() {
		return registeredYear;
	}
	public void setRegisteredYear(String registeredYear) {
		this.registeredYear = registeredYear;
	}
	public String getRegisteredCity() {
		return registeredCity;
	}
	public void setRegisteredCity(String registeredCity) {
		this.registeredCity = registeredCity;
	}
	public String getRegisteredState() {
		return registeredState;
	}
	public void setRegisteredState(String registeredState) {
		this.registeredState = registeredState;
	}
	public String getRcDoc() {
		return rcDoc;
	}
	public void setRcDoc(String rcDoc) {
		this.rcDoc = rcDoc;
	}
	public String getInsuranceDoc() {
		return insuranceDoc;
	}
	public void setInsuranceDoc(String insuranceDoc) {
		this.insuranceDoc = insuranceDoc;
	}
	public String getCabPhoto() {
		return cabPhoto;
	}
	public void setCabPhoto(String cabPhoto) {
		this.cabPhoto = cabPhoto;
	}
	public String getCabOwnerId() {
		return cabOwnerId;
	}
	public void setCabOwnerId(String cabOwnerId) {
		this.cabOwnerId = cabOwnerId;
	}
	public String getCabGenId() {
		return cabGenId;
	}
	public void setCabGenId(String cabGenId) {
		this.cabGenId = cabGenId;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getPincode() {
		return pincode;
	}
	public void setPincode(String pincode) {
		this.pincode = pincode;
	}
	public String getCabPlateStatus() {
		return cabPlateStatus;
	}
	public void setCabPlateStatus(String cabPlateStatus) {
		this.cabPlateStatus = cabPlateStatus;
	}
	public String getCabExpiryDate() {
		return cabExpiryDate;
	}
	public void setCabExpiryDate(String cabExpiryDate) {
		this.cabExpiryDate = cabExpiryDate;
	}
	public int getCabValidDays() {
		return cabValidDays;
	}
	public void setCabValidDays(int cabValidDays) {
		this.cabValidDays = cabValidDays;
	}
	public Long getMemberShipPlanId() {
		return memberShipPlanId;
	}
	public void setMemberShipPlanId(Long memberShipPlanId) {
		this.memberShipPlanId = memberShipPlanId;
	}
	public String getCabAddedDate() {
		return cabAddedDate.toString();
	}
	public void setCabAddedDate(LocalDate cabAddedDate) {
		this.cabAddedDate = cabAddedDate;
	}
    

}

