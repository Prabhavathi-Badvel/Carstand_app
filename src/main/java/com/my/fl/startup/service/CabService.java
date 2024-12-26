package com.my.fl.startup.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.my.fl.startup.entity.*;
import com.my.fl.startup.repo.*;
import com.my.fl.startup.utility.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.fl.startup.config.AWSConfig;
import com.my.fl.startup.entity.enums.Status;
import com.my.fl.startup.exception.ResourceNotFoundException;
import com.my.fl.startup.exception.ServiceException;
import com.my.fl.startup.model.AssignDriverCabModel;
import com.my.fl.startup.model.CabModel;
import com.my.fl.startup.model.ResponseModel;

@Service
public class CabService {

	@Autowired
	CabRepo cabRepo;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	private AWSConfig awsConfig;

	@Autowired
	MembershipPlanRepo membershipPlanRepo;

	@Autowired
	AssignDriverCabRepo assignDriverCabRepo;

	@Autowired
	AddDriverRepo addDriverRepo;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	private CabVerificationFeedbackRepo cabVerificationFeedbackRepo;

	@Autowired
	EmailService emailService;

	@Autowired
	SecurityUtils securityUtils;

	@Autowired
	AdminEmailSettingsRepo adminEmailSettingsRepo;

	@Autowired
	AdminSMSSettings adminSMSSettings;

	@Autowired
	SmsHandler smsHandler;

	public ResponseModel addCab(CabModel request, UserPrinciple oauth) {
		ResponseModel response = new ResponseModel();
		try {

			AddCab cabExit = cabRepo.findBycabRegNo(request.getCabRegNo());
			if (cabExit != null) {
				response.setError("true");
				response.setMsg(request.getCabRegNo() + " already register");
				return response;
			}
			request.setCabAddedDate(LocalDate.now());
			AddCab cabEntity = objectMapper.convertValue(request, AddCab.class);
			cabEntity.setCabOwnerId(oauth.getCandidateID());
			String random = String.valueOf(((int) (Math.random() * (1000000 - 100000))) + 100000);
//			LocalDate localCurrentDate = LocalDate.now();
//
//			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//			String formattedDate = localCurrentDate.format(formatter);
			cabEntity.setCabGenId("ZO" + cabEntity.getCabRegNo() + random);
			// changed status string type to enum
			cabEntity.setStatus(Status.INACTIVE);
			cabRepo.save(cabEntity);

			String cabRegNo = cabEntity.getCabRegNo();
			String userEmail = SecurityUtils.getLoggedInUserEmail();
			AdminEmailSettingsEntity emailDetails = adminEmailSettingsRepo.findById(2L).get();
			String formattedEmailBody = String.format(emailDetails.getEmailBody(), cabRegNo);
			emailService.sendEmailMessage(userEmail, formattedEmailBody, emailDetails.getEmailSubject());

			String Phone = securityUtils.getLoggedInUserPhoneNumber();
			AdminSMSEntity smsDetails = adminSMSSettings.findById(1L).get();
			String formattedSMS = String.format(smsDetails.getSmsMessage(), cabRegNo);
			smsHandler.sendCustomMessage(Phone, formattedSMS);

			response.setId(cabEntity.getCabSeqId());
			response.setError("false");
			response.setMsg("Added Successfully");
			response.setCab(cabEntity);
		} catch (Exception ex) {
			response.setError("true");
			response.setMsg("Something went wrong");
			ex.printStackTrace();
		}
		return response;
	}

	public ResponseEntity<ResponseModel> updateCab(CabModel request) {
		ResponseModel response = new ResponseModel();
		if (request.getCabGenId() == null) {
			return new ResponseEntity<ResponseModel>(new ResponseModel("Cab id can't be null", "true"),
					HttpStatus.BAD_REQUEST);
		}

		MembershipPlan membershipPlan = membershipPlanRepo.findById(request.getMemberShipPlanId()).get();

		if (membershipPlan != null) {
			int durationInDays = membershipPlan.getDuration();

			AddCab cabEntity = cabRepo.findByCabGenId(request.getCabGenId());
			if (cabEntity == null) {
				return new ResponseEntity<ResponseModel>(
						new ResponseModel("You don't have access to view this cab detial", "true"),
						HttpStatus.BAD_REQUEST);
			}

//			String cabAddDateAsString = cabEntity.getCabAddedDate();
//			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//			LocalDate cabAddDate = LocalDate.parse(cabAddDateAsString, dateFormatter);
//			LocalDate expiryDate = cabAddDate.plusDays(durationInDays);

			LocalDate cabAddDate = cabEntity.getCabAddedDate();
			LocalDate expiryDate = cabAddDate.plusDays(durationInDays);

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			String formattedExpiryDate = expiryDate.format(formatter);

			cabEntity.setCabExpiryDate(formattedExpiryDate);
			cabEntity.setStatus(Status.ACTIVE);

			cabRepo.save(cabEntity);

			response.setError("false");
			response.setMsg("updated successfully");

			String cabRegNo = cabEntity.getCabRegNo();
			String userEmail = SecurityUtils.getLoggedInUserEmail();
			AdminEmailSettingsEntity emailDetails = adminEmailSettingsRepo.findById(3L).get();
			String formattedEmailBody = String.format(emailDetails.getEmailBody(), cabRegNo);
			emailService.sendEmailMessage(userEmail, formattedEmailBody, emailDetails.getEmailSubject());

			String Phone = securityUtils.getLoggedInUserPhoneNumber();
			AdminSMSEntity smsDetails = adminSMSSettings.findById(1L).get();
			String formattedSMS = String.format(smsDetails.getSmsMessage(), cabRegNo);
			smsHandler.sendCustomMessage(Phone, formattedSMS);

			response.setCab(cabEntity);

		} else {
			response.setError("true");
		}

		return new ResponseEntity<ResponseModel>(response, HttpStatus.OK);
	}

	/*
	 * public ResponseEntity<ResponseModel> updateCab(CabModel request) {
	 * ResponseModel response = new ResponseModel(); if (request.getCabGenId() ==
	 * null) { return new ResponseEntity<ResponseModel>(new
	 * ResponseModel("Cab id can't be null", "true"), HttpStatus.BAD_REQUEST); }
	 * 
	 * MembershipPlan membershipPlan =
	 * membershipPlanRepo.findById(request.getMemberShipPlanId()).get();
	 * 
	 * AddCab cabEntity = cabRepo.findById(request.getCabSeqId()).get(); String
	 * cabAddDateAsString = cabEntity.getCabAddedDate(); DateTimeFormatter
	 * dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); LocalDate
	 * cabAddDate = LocalDate.parse(cabAddDateAsString, dateFormatter); LocalDate
	 * expiryDate = cabAddDate.plusDays(durationInDays);
	 * 
	 * DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	 * String formattedExpiryDate = expiryDate.format(formatter);
	 * 
	 * cabEntity.setCabExpiryDate(formattedExpiryDate); // changed status string
	 * type to enum cabEntity.setStatus(CabStatus.ACTIVE);
	 * 
	 * cabRepo.save(cabEntity); response.setError("false");
	 * response.setMsg("updated successfully"); response.setCab(cabEntity); if
	 * (membershipPlan != null) { int durationInDays =
	 * Integer.parseInt(membershipPlan.getDuration());
	 * 
	 * AddCab cabEntity = cabRepo.findByCabGenId(request.getCabGenId()); if
	 * (cabEntity == null) { return new ResponseEntity<ResponseModel>( new
	 * ResponseModel("You don't have access to view this cab detial", "true"),
	 * HttpStatus.BAD_REQUEST); }
	 * 
	 * String cabAddDateAsString = cabEntity.getCabAddedDate(); DateTimeFormatter
	 * dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); LocalDate
	 * cabAddDate = LocalDate.parse(cabAddDateAsString, dateFormatter); LocalDate
	 * expiryDate = cabAddDate.plusDays(durationInDays);
	 * 
	 * DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	 * String formattedExpiryDate = expiryDate.format(formatter);
	 * 
	 * cabEntity.setCabExpiryDate(formattedExpiryDate);
	 * cabEntity.setStatus("active");
	 * 
	 * cabRepo.save(cabEntity); response.setError("false");
	 * response.setMsg("updated successfully"); response.setCab(cabEntity);
	 * 
	 * } else { response.setError("true"); }
	 * 
	 * return new ResponseEntity<ResponseModel>(response, HttpStatus.BAD_REQUEST); }
	 */

	public ResponseModel inactiveCab(CabModel request) {
		try {
			AddCab cabEntity = cabRepo.findById(request.getCabSeqId()).get();
			cabEntity.setStatus(request.getStatus());

			cabRepo.save(cabEntity);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return new ResponseModel("Cab Updated Successfully", null);
	}

	public ResponseEntity<?> uploadCabImage(Long cabId, MultipartFile image) {
		AddCab cab = cabRepo.findById(cabId).orElse(null);
		ResponseModel response = new ResponseModel();
		if (cab == null) {
			response.setErrorMsg("Something went wrong");
			return new ResponseEntity<ResponseModel>(response, HttpStatus.BAD_REQUEST);
		}

		String directoryPath = "cab/" + cabId + "/";
		Map<String, MultipartFile> docMap = new HashMap<>();
		String cabPicPath = directoryPath + image.getOriginalFilename();

		docMap.put(cabPicPath, image);

		if (awsConfig.uploadFileToS3Bucket(docMap)) {
			cab.setCabPhoto(cabPicPath);
			cabRepo.save(cab);
			response.setMsg("Cab Photo Uploaded Successfully..");
		}
		return new ResponseEntity<ResponseModel>(response, HttpStatus.OK);
	}

	public ResponseEntity<?> uploadCabDocs(Long seqCabId, MultipartFile rcDoc, MultipartFile insuranceDocument,
			MultipartFile cabPhoto) {
		AddCab cab = cabRepo.findById(seqCabId).orElse(null);

		if (cab == null) {
			return new ResponseEntity<ResponseModel>(new ResponseModel("Something went wrong", null),
					HttpStatus.BAD_REQUEST);
		}

		String directoryPath = "cab/" + seqCabId + "/";
		Map<String, MultipartFile> docMap = new HashMap<>();
		String rdDocumentPath = null;
		if (rcDoc != null) {
			rdDocumentPath = directoryPath + rcDoc.getOriginalFilename();
			docMap.put(rdDocumentPath, rcDoc);
		}

		String insuranceDocPath = null;
		if (insuranceDocument != null) {
			insuranceDocPath = directoryPath + insuranceDocument.getOriginalFilename();
			docMap.put(insuranceDocPath, insuranceDocument);
		}

		String cabPicPath = null;
		if (cabPhoto != null) {
			cabPicPath = directoryPath + cabPhoto.getOriginalFilename();
			docMap.put(cabPicPath, cabPhoto);
		}

		ResponseModel response = new ResponseModel();
		if (awsConfig.uploadFileToS3Bucket(docMap)) {
			if (insuranceDocPath != null) {
				cab.setInsuranceDoc(insuranceDocPath);
			}

			if (rdDocumentPath != null) {
				cab.setRcDoc(rdDocumentPath);
			}

			if (cabPicPath != null) {
				cab.setCabPhoto(cabPicPath);
			}

			cabRepo.save(cab);
			response.setMsg("Cab Related Uploaded Successfully..");
		}
		return new ResponseEntity<ResponseModel>(response, HttpStatus.OK);
	}

	public Map<String, Object> getCab(String cabId, String ownerId) {
		CabModel cabModel = new CabModel();
		String message = null;
		String status = null;
		AddCab cab = cabRepo.findByCabRegNoAndCabOwnerId(cabId, ownerId);
		if (cab == null) {
			throw new ResourceNotFoundException("Cab not found: The requested cab does not exist");
		}
		Optional<CabVerificationFeedbackEntity> cabVerificationFeedbackEntity = cabVerificationFeedbackRepo
				.findByCabGenId(cab.getCabGenId());
		if (cabVerificationFeedbackEntity.isEmpty()
				|| !cabVerificationFeedbackEntity.get().getVerificationFeedback().equals("VERIFIED")) {
			status = "UNVERIFIED";
			message = "cab is not verified";
		} else {
			status = "VERIFIED";
			message = "cab is verified";
		}

		try {
			cabModel = objectMapper.convertValue(cab, CabModel.class);
		} catch (Exception e) {
			throw new ServiceException("Something went wrong" + HttpStatus.BAD_REQUEST);
		}
		cabModel.setInsuranceDoc(awsConfig.getUrl(cab.getInsuranceDoc()));
		cabModel.setRcDoc(awsConfig.getUrl(cab.getRcDoc()));
		cabModel.setCabPhoto(awsConfig.getUrl(cab.getCabPhoto()));
		return Map.of("data", cabModel, "message", message, "status", status);
	}

	public ResponseEntity<?> getCabs(String userId, String brand, String model, LocalDate startDate, LocalDate endDate) {
	    // Call the repository with the correct query method
	    List<AddCab> cabs = cabRepo.findByUserIdOrBrandOrModelOrStartDateBetweenAndEndDateBetween(userId, brand, model, startDate, endDate);

	    // Check if the list is empty
	    if (cabs.isEmpty()) {
	        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	    }

	    // Map AddCab entities to CabModel using ObjectMapper or manually
	    List<CabModel> response =cabs.stream().map(cab -> {
			CabModel cb = objectMapper.convertValue(cab, CabModel.class);
			cb.setInsuranceDoc(cb.getInsuranceDoc() != null ? cb.getInsuranceDoc() : "no documents found");
			cb.setCabPhoto(cb.getCabPhoto() != null ? cb.getCabPhoto() : "no documents found");
			cb.setRcDoc(cb.getRcDoc() != null ? cb.getRcDoc() : "no documents found");
			return cb;
		}).collect(Collectors.toList());

	    // Return the response with status OK
	    return new ResponseEntity<>(response, HttpStatus.OK);
	}


	public ResponseEntity<?> getCabs1(String userId, String brand, String model, LocalDate startDate,
			LocalDate endDate) {

		StringBuilder sql = new StringBuilder("Select * From add_cab WHERE  CAB_OWNER_ID = ? ");

		if (brand != null && !brand.isEmpty()) {
			sql.append(" AND CAB_BRAND = '");
			sql.append(brand);
			sql.append("'");
		}

		if (model != null && !model.isEmpty()) {
			sql.append(" AND CAB_MODEL = '");
			sql.append(model);
			sql.append("'");
		}

		if (startDate != null && endDate != null) {
			sql.append(" AND ( DATE(CAB_ADDED_DATE ) BETWEEN  ( '" + startDate + "' ) AND ('" + endDate + "' ) ) ");
		} else if (startDate != null) {
			sql.append(" AND DATE(CAB_ADDED_DATE ) >= ( '" + startDate + "' ) ");
		} else if (endDate != null) {
			sql.append(" AND DATE(CAB_ADDED_DATE )  <= ( '" + endDate + "') ");
		}

		System.out.println(sql.toString());

		List<CabModel> response = jdbcTemplate.query(sql.toString(), new RowMapper<CabModel>() {
			@Override
			public CabModel mapRow(ResultSet rs, int arg1) throws SQLException {
				CabModel cabModel = new CabModel();
				cabModel.setCabSeqId(rs.getLong("CAB_SEQ_ID"));
				cabModel.setCabRegNo(rs.getString("CAB_REG_NO"));
				cabModel.setMobileNo(rs.getString("MOBILE_NO"));
				cabModel.setAddress(rs.getString("ADDRESS"));
				cabModel.setCity(rs.getString("CITY"));
				cabModel.setState(rs.getString("STATE"));
				cabModel.setDistrict(rs.getString("DISTRICT"));
				cabModel.setPincode(rs.getString("PINCODE"));
				cabModel.setInsuranceDoc(awsConfig.getUrl(rs.getString("INSURANCE_DOC")));
				cabModel.setRcDoc(awsConfig.getUrl(rs.getString("RC_DOC")));
				cabModel.setCabPhoto(awsConfig.getUrl(rs.getString("CAB_PHOTO")));
				cabModel.setStatus(Status.valueOf(rs.getString("STATUS")));
				cabModel.setCabBrand(rs.getString("CAB_BRAND"));
				cabModel.setCabModel(rs.getString("CAB_MODEL"));
				cabModel.setModelYear(rs.getString("MODEL_YEAR"));
				cabModel.setCurrentMileage(rs.getString("CURRENT_MILEAGE"));
				cabModel.setFuelType(rs.getString("FUEL_TYPE"));
				cabModel.setBodyType(rs.getString("BODY_TYPE"));
				cabModel.setTransmission(rs.getString("TRANSMISSION"));
				cabModel.setKmDriven(rs.getString("KM_DRIVEN"));
				cabModel.setNumberOfPassenger(rs.getString("NO_OF_PASSENGER"));
				cabModel.setColor(rs.getString("COLOR"));
				cabModel.setInsuranceCompanyName(rs.getString("INSURENCE_COMP_NAME"));
				cabModel.setCertifiedCompanyName(rs.getString("CERTIFIED_COMP_NAME"));
				cabModel.setRegisteredYear(rs.getString("REGISTERED_YEAR"));
				cabModel.setRegisteredCity(rs.getString("REGISTERED_CITY"));
				cabModel.setRegisteredState(rs.getString("REGISTERED_STATE"));
				cabModel.setCabGenId(rs.getString("CAB_GEN_ID"));
				cabModel.setCabOwnerId(rs.getString("CAB_OWNER_ID"));

				String cabAddedDateString = rs.getString("CAB_ADDED_DATE");
				if (cabAddedDateString != null) {
					cabModel.setCabAddedDate(LocalDate.parse(cabAddedDateString));
				}
				// cabModel.setCabAddedDate(rs.getString("CAB_ADDED_DATE"));
				cabModel.setCabPlateStatus(rs.getString("CAB_PLATE_STATUS"));
				cabModel.setCabExpiryDate(rs.getString("CAB_EXPIRY_DATE"));
				cabModel.setCabValidDays(rs.getInt("CAB_VALID_DAYS"));
				return cabModel;
			}
		}, new Object[] { userId });

		return new ResponseEntity<List<CabModel>>(response, HttpStatus.OK);
	}

	public ResponseEntity<?> assignCabDriver(AssignDriverCabModel request) {

		if (request.getCabId() != null && request.getDriverId() != null) {
			AddCab cab = cabRepo.findById(request.getCabId()).orElse(null);

			if (cab == null) {
				return new ResponseEntity<ResponseModel>(new ResponseModel("Something went wrong", null),
						HttpStatus.BAD_REQUEST);
			}

			AddDriverEntity addDriverEntity = addDriverRepo.findById(request.getDriverId()).orElse(null);
			if (addDriverEntity == null) {
				return new ResponseEntity<ResponseModel>(new ResponseModel("Something went wrong", null),
						HttpStatus.BAD_REQUEST);
			}

			AssignDriverCab assignDriverCab = new AssignDriverCab();
			assignDriverCab.setCabId(cab.getCabGenId());
			assignDriverCab.setCabOwnerId(cab.getCabOwnerId());
			assignDriverCab.setDriverId(addDriverEntity.getDriverId());
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
			assignDriverCab.setAssignDate(dateFormat.format(new Date()));
			assignDriverCab.setAssignTime(timeFormat.format(new Date()));
			assignDriverCab.setStatus("ACTIVE");
			assignDriverCabRepo.save(assignDriverCab);
			return new ResponseEntity<ResponseModel>(new ResponseModel("Successfully Assign Cab Driver"),
					HttpStatus.OK);
		}

		return new ResponseEntity<ResponseModel>(new ResponseModel("Something went wrong"), HttpStatus.BAD_REQUEST);
	}

	public ResponseEntity<ResponseModel> updateCabDetail(CabModel request, String ownerId) {
		if (request.getCabGenId() == null) {
			return new ResponseEntity<ResponseModel>(new ResponseModel("Cab id can't be null", "true"),
					HttpStatus.BAD_REQUEST);
		}

		AddCab cab = cabRepo.findByCabGenIdAndCabOwnerId(request.getCabGenId(), ownerId);
		if (cab == null) {
			return new ResponseEntity<ResponseModel>(
					new ResponseModel("You don't have access to view this cab detial", "true"), HttpStatus.BAD_REQUEST);
		}

		if (cab.getStatus().equals("Verified")) {
			return new ResponseEntity<ResponseModel>(
					new ResponseModel("Sorry you cab already verified now you can't update any detail", "true"),
					HttpStatus.BAD_REQUEST);
		}

		if (request.getMobileNo() != null) {
			cab.setMobileNo(request.getMobileNo());
		}
		if (request.getAddress() != null) {
			cab.setAddress(request.getAddress());
		}
		if (request.getCity() != null) {
			cab.setCity(request.getCity());
		}
		if (request.getState() != null) {
			cab.setState(request.getState());
		}
		if (request.getDistrict() != null) {
			cab.setDistrict(request.getDistrict());
		}
		if (request.getPincode() != null) {
			cab.setPincode(request.getPincode());
		}
		if (request.getCabBrand() != null) {
			cab.setCabBrand(request.getCabBrand());
		}
		if (request.getCabModel() != null) {
			cab.setCabModel(request.getCabModel());
		}
		if (request.getModelYear() != null) {
			cab.setModelYear(request.getModelYear());
		}
		if (request.getCurrentMileage() != null) {
			cab.setCurrentMileage(request.getCurrentMileage());
		}
		if (request.getFuelType() != null) {
			cab.setFuelType(request.getFuelType());
		}
		if (request.getBodyType() != null) {
			cab.setBodyType(request.getBodyType());
		}
		if (request.getTransmission() != null) {
			cab.setTransmission(request.getTransmission());
		}
		if (request.getKmDriven() != null) {
			cab.setKmDriven(request.getKmDriven());
		}
		if (request.getNumberOfPassenger() != null) {
			cab.setNumberOfPassenger(request.getNumberOfPassenger());
		}
		if (request.getColor() != null) {
			cab.setColor(request.getColor());
		}
		if (request.getInsuranceCompanyName() != null) {
			cab.setInsuranceCompanyName(request.getInsuranceCompanyName());
		}
		if (request.getCertifiedCompanyName() != null) {
			cab.setCertifiedCompanyName(request.getCertifiedCompanyName());
		}
		if (request.getRegisteredYear() != null) {
			cab.setRegisteredYear(request.getRegisteredYear());
		}
		if (request.getRegisteredCity() != null) {
			cab.setRegisteredCity(request.getRegisteredCity());
		}
		if (request.getRegisteredState() != null) {
			cab.setRegisteredState(request.getRegisteredState());
		}
		if (request.getCabExpiryDate() != null) {
			cab.setCabExpiryDate(request.getCabExpiryDate());
		}
		if (request.getCabValidDays() != 0) {
			cab.setCabValidDays(request.getCabValidDays() + "");
		}

		if (request.getCabRegNo() != null) {

			AddCab cabExit = cabRepo.findBycabRegNo(request.getCabRegNo());
			if (cabExit != null && !cabExit.getCabGenId().equals(cab.getCabGenId())) {
				return new ResponseEntity<ResponseModel>(
						new ResponseModel(request.getCabRegNo() + " already register", "true"), HttpStatus.BAD_REQUEST);
			}

			cab.setCabRegNo(request.getCabRegNo());
		}

		cabRepo.save(cab);
		return new ResponseEntity<ResponseModel>(new ResponseModel("Successfully Cab Updated"), HttpStatus.OK);
	}

}
