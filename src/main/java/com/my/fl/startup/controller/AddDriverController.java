package com.my.fl.startup.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.my.fl.startup.model.AddDriverModel;
import com.my.fl.startup.model.AddDriverResponseGet;
import com.my.fl.startup.model.PaginatedResponse;
import com.my.fl.startup.service.AddDriverService;

@RestController
@RequestMapping("/api/addDriver")
public class AddDriverController {

	@Autowired
	AddDriverService addDriverService;

	@PostMapping("/add-driver")
	public ResponseEntity<?> addDriver(@RequestBody AddDriverModel addDriverModel) {
		return addDriverService.addDriver(addDriverModel);
	}

	@PutMapping("/update")
	public ResponseEntity<?> updateDriver(@RequestBody AddDriverModel updateDriverModel) {
		ResponseEntity<?> response = addDriverService.updateDriver(updateDriverModel);
		return new ResponseEntity<>(response.getBody(), response.getStatusCode());
	}

//	@PostMapping("uploadDriverDocuments")
//	public ResponseEntity<?> uploadDriverDocuments(@RequestParam("seqDriverId") Long seqDriverId,
//			@RequestParam("rcDocument") MultipartFile rcDoc,
//			@RequestParam("drivers_licence") MultipartFile driversLicence,
//			@RequestParam("insurance_document") MultipartFile insuranceDocument,
//			@RequestParam("cabPhoto") MultipartFile cabPhoto, @RequestParam("aadhar_card") MultipartFile aadharCard) {
//
//		return addDriverService.uploadDriverDocs(seqDriverId, rcDoc, driversLicence, insuranceDocument, cabPhoto,
//				aadharCard);
//	}

	@PostMapping("upload-driver-docs")
	public ResponseEntity<?> uploadDriverDocs(@RequestParam String driverGenId,
			@RequestParam(value = "rcDocument", required = false) MultipartFile rcDoc,
			@RequestParam(value = "drivers_licence", required = false) MultipartFile driversLicence,
			@RequestParam(value = "insurance_document", required = false) MultipartFile insuranceDocument,
			@RequestParam(value = "driverPhoto", required = false) MultipartFile driverPhoto,
			@RequestParam(value = "aadhar_card", required = false) MultipartFile aadharCard) {
		return addDriverService.uploadDriverDocs(driverGenId, rcDoc, driversLicence, insuranceDocument, driverPhoto,
				aadharCard);
	}

	@GetMapping("get-driver")
	public ResponseEntity<?> getDriver(@RequestParam(required = false) String driverId) {

		return addDriverService.getDriver(driverId);
	}

	@GetMapping("get-driver-details")
	// Pagination need
	public ResponseEntity<PaginatedResponse<AddDriverResponseGet>> getDriverDetails(
			@RequestParam(required = false) String city, @RequestParam(required = false) String mobileNo,
			@RequestParam(required = false) String email, @RequestParam(required = false) String driverId,
			@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "pageNo", required = false) Integer pageNo) {
		return new ResponseEntity<>(addDriverService.getDriverDetails(city, mobileNo, email, driverId, startDate,
				endDate, pageSize, pageNo), HttpStatus.OK);
	}

}
