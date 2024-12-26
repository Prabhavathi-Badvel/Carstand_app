package com.my.fl.startup.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.my.fl.startup.entity.AdminSMSEntity;
import com.my.fl.startup.entity.enums.Status;
import com.my.fl.startup.repo.AdminSMSSettings;
import com.my.fl.startup.utility.SecurityUtils;
import com.my.fl.startup.utils.Base64MultipartFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.fl.startup.config.AWSConfig;
import com.my.fl.startup.entity.AddCab;
import com.my.fl.startup.entity.AddDriverEntity;
import com.my.fl.startup.model.AddDriverModel;
import com.my.fl.startup.model.AddDriverResponseGet;
import com.my.fl.startup.model.CabModel;
import com.my.fl.startup.model.PaginatedResponse;
import com.my.fl.startup.model.ResponseModel;
import com.my.fl.startup.repo.AddDriverRepo;

@Service
public class AddDriverService {

	@Autowired
	AddDriverRepo addDriverRepo;

	@Autowired
	private AWSConfig awsConfig;

	@Autowired
	EmailService emailService;

	@Autowired
	SecurityUtils securityUtils;

	@Autowired
	AdminSMSSettings adminSMSSettings;

	@Autowired
	SmsHandler smsHandler;

	@Autowired
	ObjectMapper objectMapper;

	public ResponseEntity<ResponseModel> addDriver(AddDriverModel addDriverModel) {
		AddDriverEntity addDriverEntity = new AddDriverEntity();
		ResponseModel response = new ResponseModel();
		try {
			String mUserId = extractCabOwnerIdFromSecurityContext();
			String driverId = generateDriverId();
			mapModelToEntity(addDriverModel, addDriverEntity, mUserId, driverId);
			addDriverRepo.save(addDriverEntity);
			response.setError("false");
			response.setMsg("Successfully Added");
			securityUtils.sendEmailTemplate(SecurityUtils.getLoggedInUserEmail(), 7L);
			String Phone = securityUtils.getLoggedInUserPhoneNumber();
			AdminSMSEntity smsDetails = adminSMSSettings.findById(1L).get();
			String formattedSMS = smsDetails.getSmsMessage();
			smsHandler.sendCustomMessage(Phone, formattedSMS);
			response.setAddedDriver(addDriverEntity);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setError("true");
			response.setMsg("Something went wrong");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
	}

	private void mapModelToEntity(AddDriverModel model, AddDriverEntity entity, String mUserId, String driverId) {
		entity.setFirstName(model.getFirstName());
		entity.setLastName(model.getLastName());
		entity.setEmail(model.getEmail());
		entity.setMobileNo(model.getMobileNo());
		entity.setDob(model.getDob());
		entity.setAddress(model.getAddress());
		entity.setStreet(model.getStreet());
		entity.setCity(model.getCity());
		entity.setState(model.getState());
		entity.setDistrict(model.getDistrict());
		entity.setPincode(model.getPincode());
		entity.setRegisteredState(model.getRegisteredState());
		entity.setLicenseNo(model.getLicenseNo());
		entity.setLicenseType(model.getLicenseType());
		entity.setExpiryDate(model.getExpiryDate());
		entity.setDrivingExp(model.getDrivingExp());
		entity.setPermitType(model.getPermitType());
		entity.setWithinRange(model.getWithinRange());
		entity.setAdharNo(model.getAdharNo());
		entity.setPanNo(model.getPanNo());
		entity.setMUserId(mUserId);
		entity.setAvailability(model.getAvailability());
		entity.setDriverId(driverId);
		entity.setStatus(Status.INACTIVE.toString());
		entity.setJobType(model.getJobType());
		entity.setRegisteredDate(model.getRegisteredDate());
		entity.setCommercialLicense(model.getCommercialLicense());
		entity.setWorkedLocation(model.getWorkedLocation());

		// AWS Image upload
		String directoryPath = "driver/" + entity.getDriverId() + "/";
		Map<String, MultipartFile> docMap = new HashMap<>();
		if (model.getLicenseDoc() != null) {
			String licDocName = "lic_doc_" + System.currentTimeMillis();
			MultipartFile licDocImage = Base64MultipartFile.fromBase64(model.getLicenseDoc(), licDocName);
			String imagePath = directoryPath + licDocImage.getOriginalFilename();
			entity.setLicenseDoc(awsConfig.uploadFileToS3Bucket(imagePath, licDocImage));
		}

		if (model.getPhoto() != null) {
			String photoName = "profile_doc_" + System.currentTimeMillis();
			MultipartFile photoImage = Base64MultipartFile.fromBase64(model.getPhoto(), photoName);
			String imagePath = directoryPath + photoImage.getOriginalFilename();
			entity.setPhoto(awsConfig.uploadFileToS3Bucket(imagePath, photoImage));
		}

		if (model.getInsuranceDoc() != null) {
			String insuName = "insu_doc_" + System.currentTimeMillis();
			MultipartFile insuImage = Base64MultipartFile.fromBase64(model.getInsuranceDoc(), insuName);
			String imagePath = directoryPath + insuImage.getOriginalFilename();
			docMap.put(imagePath, insuImage);
			entity.setInsuranceDoc(awsConfig.uploadFileToS3Bucket(imagePath, insuImage));
		}

		if (model.getAdharDoc() != null) {
			String adharName = "adhar_doc_" + System.currentTimeMillis();
			MultipartFile adharImage = Base64MultipartFile.fromBase64(model.getAdharDoc(), adharName);
			String imagePath = directoryPath + adharImage.getOriginalFilename();
			entity.setAdharDoc(awsConfig.uploadFileToS3Bucket(imagePath, adharImage));
		}

		if (model.getRcDoc() != null) {
			String rcName = "rc_doc_" + System.currentTimeMillis();
			MultipartFile rcDocImage = Base64MultipartFile.fromBase64(model.getRcDoc(), rcName);
			String imagePath = directoryPath + rcDocImage.getOriginalFilename();
			entity.setRcDoc(awsConfig.uploadFileToS3Bucket(imagePath, rcDocImage));
		}
	}

	private String generateDriverId() {
		// Generate driver ID in the format "DR-dateandtimer"
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		String dateAndTime = now.format(formatter);
		return "DR" + dateAndTime;
	}

	public ResponseEntity<ResponseModel> getDriver(String driverId) {
		List<AddDriverEntity> addDriverEntities = new ArrayList<>();
		ResponseModel response = new ResponseModel();
		try {
			String mUserId = extractCabOwnerIdFromSecurityContext();

			if (driverId != null) {
				addDriverEntities = addDriverRepo.findByMUserIdDriverId(mUserId, driverId);
			} else {
				addDriverEntities = addDriverRepo.findByMUserId(mUserId);
			}
			List<AddDriverModel> addDriverModelList = addDriverEntities.stream().map(driver -> {
				AddDriverModel adm = objectMapper.convertValue(driver, AddDriverModel.class);
				adm.setInsuranceDoc(adm.getInsuranceDoc() != null ? adm.getInsuranceDoc() : "no documents found");
				adm.setPhoto(adm.getPhoto() != null ? adm.getPhoto() : "no documents found");
				adm.setRcDoc(adm.getRcDoc() != null ? adm.getRcDoc() : "no documents found");
				adm.setLicenseDoc(adm.getLicenseDoc() != null ? adm.getLicenseDoc() : "no documents found");
				adm.setAdharDoc(adm.getAdharDoc() != null ? adm.getAdharDoc() : "no documents found");
				return adm;
			}).collect(Collectors.toList());

			response.setMsg("Here the list");
			response.setError("false");
			response.setData(addDriverModelList);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			response.setError("true");
			response.setMsg("There is no driver in your list");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
	}

//	public ResponseEntity<?> getDriver(Long driverId) {
//		List<AddDriverModel> addDriverModelList = new ArrayList<>();
//		ResponseModel response =new ResponseModel();
//		try {
//			String mUserId = extractCabOwnerIdFromSecurityContext();
//
//			AddDriverEntity addDriverEntity = addDriverRepo.findById(driverId)
//					.orElseThrow(() -> new UsernameNotFoundException("Driver not found with id: " + driverId));
//
//			if (!addDriverEntity.getMUserId().equals(mUserId)) {
//				return new ResponseEntity<>(new ResponseModel("Unauthorized", "You are not authorized to view this driver"), HttpStatus.UNAUTHORIZED);
//			}
//
//			AddDriverModel addDriverModel = new AddDriverModel();
//			addDriverModel.setSeqDriverId(addDriverEntity.getSeqDriverId());
//			addDriverModel.setFirstName(addDriverEntity.getFirstName());
//			addDriverModel.setLastName(addDriverEntity.getLastName());
//			addDriverModel.setEmail(addDriverEntity.getEmail());
//			addDriverModel.setMobileNo(addDriverEntity.getMobileNo());
//			addDriverModel.setDob(addDriverEntity.getDob());
//			addDriverModel.setAddress(addDriverEntity.getAddress());
//			addDriverModel.setStreet(addDriverEntity.getStreet());
//			addDriverModel.setCity(addDriverEntity.getCity());
//			addDriverModel.setState(addDriverEntity.getState());
//			addDriverModel.setDistrict(addDriverEntity.getDistrict());
//			addDriverModel.setPincode(addDriverEntity.getPincode());
//			addDriverModel.setRegisteredState(addDriverEntity.getRegisteredState());
//			addDriverModel.setLicenseNo(addDriverEntity.getLicenseNo());
//			addDriverModel.setLicenseType(addDriverEntity.getLicenseType());
//			addDriverModel.setExpiryDate(addDriverEntity.getExpiryDate());
//			addDriverModel.setDrivingExp(addDriverEntity.getDrivingExp());
//			addDriverModel.setPermitType(addDriverEntity.getPermitType());
//			addDriverModel.setWithinRange(addDriverEntity.getWithinRange());
//			addDriverModel.setAdharNo(addDriverEntity.getAdharNo());
//			addDriverModel.setPanNo(addDriverEntity.getPanNo());
//			addDriverModel.setLicenseDoc(awsConfig.getUrl(addDriverEntity.getLicenseDoc()));
//			addDriverModel.setAdharDoc(awsConfig.getUrl(addDriverEntity.getAdharDoc()));
//			addDriverModel.setInsuranceDoc(awsConfig.getUrl(addDriverEntity.getInsuranceDoc()));
//			addDriverModel.setRcDoc(awsConfig.getUrl(addDriverEntity.getRcDoc()));
//			addDriverModel.setPhoto(awsConfig.getUrl(addDriverEntity.getPhoto()));
//			addDriverModel.setMUserId(addDriverEntity.getMUserId());
//			addDriverModel.setAvailability(addDriverEntity.getAvailability());
//			addDriverModel.setDriverId(addDriverEntity.getDriverId());
//			addDriverModel.setStatus(addDriverEntity.getStatus());
//			addDriverModel.setJobType(addDriverEntity.getJobType());
//			addDriverModel.setRegisteredDate(addDriverEntity.getRegisteredDate());
//
//			addDriverModelList.add(addDriverModel);
//			response.setMsg("Here the list");
//			return new ResponseEntity<>(addDriverModelList, HttpStatus.OK);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new ResponseEntity<>("There is no driver in your list", HttpStatus.BAD_REQUEST);
//		}
//
//	}
	@Transactional
	public ResponseEntity<ResponseModel> uploadDriverDocs(String driverGenId, MultipartFile rcDoc,
			MultipartFile driversLicence, MultipartFile insuranceDocument, MultipartFile driverPhoto,
			MultipartFile aadharCard) {

		String mUserId = extractCabOwnerIdFromSecurityContext();

		AddDriverEntity driver = addDriverRepo.findByDriverId(driverGenId);

		ResponseModel response = new ResponseModel();

		if (driver == null || !driver.getMUserId().equals(mUserId)) {
			response.setError("true");
			response.setMsg("Driver not found or unauthorized access");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		String directoryPath = "driver/" + driverGenId + "/";
		if (rcDoc != null && !rcDoc.isEmpty()) {
			String rdDocumentPath = directoryPath + rcDoc.getOriginalFilename();
			String docLink = awsConfig.uploadFileToS3Bucket(rdDocumentPath, driversLicence);
			driver.setRcDoc(docLink);
		}

		if (driversLicence != null && !driversLicence.isEmpty()) {
			String driversLicencePath = directoryPath + driversLicence.getOriginalFilename();
			String lincenceLink = awsConfig.uploadFileToS3Bucket(driversLicencePath, driversLicence);
			driver.setLicenseDoc(lincenceLink);
		}

		if (insuranceDocument != null && !insuranceDocument.isEmpty()) {
			String insuranceDocPath = directoryPath + insuranceDocument.getOriginalFilename();
			String insuranceDocLink = awsConfig.uploadFileToS3Bucket(insuranceDocPath, insuranceDocument);
			driver.setInsuranceDoc(insuranceDocLink);
		}

		if (driverPhoto != null && !driverPhoto.isEmpty()) {
			String driveImagePath = directoryPath + driverPhoto.getOriginalFilename();
			String driveImageLink = awsConfig.uploadFileToS3Bucket(driveImagePath, driverPhoto);
			driver.setPhoto(driveImageLink);
		}

		if (aadharCard != null && !aadharCard.isEmpty()) {
			String aadharCardPath = directoryPath + aadharCard.getOriginalFilename();
			String aadharCardLink = awsConfig.uploadFileToS3Bucket(aadharCardPath, aadharCard);
			driver.setAdharDoc(aadharCardLink);
		}
		response.setError("false");
		response.setMsg("Driver related documents uploaded/updated successfully.");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public ResponseEntity<ResponseModel> updateDriver(AddDriverModel updateDriverModel) {
		ResponseModel response = new ResponseModel();
		try {
			String mUserId = extractCabOwnerIdFromSecurityContext();

			AddDriverEntity addDriverEntity = addDriverRepo.findByDriverId(updateDriverModel.getDriverId());

			if (addDriverEntity == null) {
				response.setError("true");
				response.setMsg("Driver not found");
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}

			if (!addDriverEntity.getMUserId().equals(mUserId)) {
				response.setError("true");
				response.setMsg("Unauthorized access");
				return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
			}

			mapModelToEntity(updateDriverModel, addDriverEntity, mUserId, addDriverEntity.getDriverId());

			addDriverRepo.save(addDriverEntity);

			/*
			 * String userEmail = SecurityUtils.getLoggedInUserEmail();
			 * AdminEmailSettingsEntity emailDetails=
			 * adminEmailSettingsRepo.findById(11L).get(); String formattedEmailBody =
			 * String.format(emailDetails.getEmailBody());
			 * emailService.sendEmailMessage(userEmail,formattedEmailBody,emailDetails.
			 * getEmailSubject());
			 */

			response.setError("false");
			response.setMsg("Successfully Updated");
			response.setAddedDriver(addDriverEntity);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			response.setError("true");
			response.setMsg("Something went wrong");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
	}

	public PaginatedResponse<AddDriverResponseGet> getDriverDetails(String city, String mobileNo, String email,
			String driverId, String startDate, String endDate, Integer parsedPageNo, Integer parsedPageSize) {

		// Extract the user ID from the security context
		String mUserId = extractCabOwnerIdFromSecurityContext();

		// Validate and set default values for page number and page size
		int pageNo = (parsedPageNo != null && parsedPageNo >= 0) ? parsedPageNo : 0;
		int pageSize = (parsedPageSize != null && parsedPageSize > 0) ? parsedPageSize : 10;

		// Create a pageable object
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		// Query the repository to fetch the driver details
		Page<AddDriverEntity> page = addDriverRepo.findDrivers(mUserId, city, mobileNo, email, driverId, startDate,
				endDate, pageable);

		// Convert entities to DTOs using ModelMapper
		List<AddDriverResponseGet> responseItems = page.getContent().stream()
				.map(entity -> objectMapper.convertValue(entity, AddDriverResponseGet.class))
				.collect(Collectors.toList());

		// Create and populate the paginated response
		PaginatedResponse<AddDriverResponseGet> paginatedResponse = new PaginatedResponse<>();
		paginatedResponse.setItems(responseItems);
		paginatedResponse.setPageSize(pageSize);
		paginatedResponse.setPageNumber(pageNo);
		paginatedResponse.setTotalPages(page.getTotalPages());
		paginatedResponse.setTotalElements(page.getTotalElements());
		return paginatedResponse;
	}

	private String extractCabOwnerIdFromSecurityContext() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			return (principal instanceof UserPrinciple && ((UserPrinciple) principal).getCandidateID() != null)
					? ((UserPrinciple) principal).getCandidateID()
					: null; // Assuming the username is the cabOwnerId
		} else {
			return principal.toString();
		}
	}

}
