package com.my.fl.startup.model;

import com.my.fl.startup.entity.*;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ResponseModel {

	private String error;
	private String msg;
	private Long id;

	private Object data;


	private List<String> brands;
	private List<CabMasterEntity> cabMasterEntities;

	private CabMasterEntity cabMaster;

	private CabRouteEntity cabRouteUpdated;

	private AddDriverEntity addedDriver;

	private List<AddDriverEntity> driveList;

	private CabCancellationPolicy cabCancellationPolicy;


	private TravellerRegistrationEntity travellerRegistration;

	@Data
	public static class TravellerRegistrationEntity{

		public String travellerId;
		private String travellerName;
		private String travellerMobile;
		private String travellerEmail;
		private String travellerMobileOtp;
		private String travellerEmailOtp;
		private String travellerMobileVerified;
		private String travellerEmailVerified;
		private String status;
		private String travelledPassword;
		private String registrationDate;
		private String userType;

	}


	private DriverPreferedRoute preferedRoute;

	private AddCab cab;



	String status;
	String errorMsg;

	public ResponseModel() {

	}

	public ResponseModel(String msg) {
		this.msg = msg;
	}

	public ResponseModel(String error, String msg, List<String> brands, List<CabMasterEntity> cabMasterEntities) {
		this.error = error;
		this.msg = msg;
		this.brands = brands;
		this.cabMasterEntities = cabMasterEntities;
	}

	public ResponseModel(String message, String error) {
		this.error = error;
		this.msg = message;
	}

}
