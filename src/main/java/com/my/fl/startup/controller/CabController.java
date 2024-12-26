package com.my.fl.startup.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.my.fl.startup.model.AssignDriverCabModel;
import com.my.fl.startup.model.CabModel;
import com.my.fl.startup.model.ResponseModel;
import com.my.fl.startup.model.UpdateStatusRequest;
import com.my.fl.startup.service.CabBookingService;
import com.my.fl.startup.service.CabService;
import com.my.fl.startup.service.UserPrinciple;

@RestController
@RequestMapping("/api/cab/")
public class CabController {

	@Autowired
	CabService cabService;

	@Autowired
	CabBookingService bookingService;

	@PostMapping("add-cab")
	public ResponseEntity<?> addCab(Authentication authentication, @RequestBody CabModel request) {
		UserPrinciple userPrincipal = (UserPrinciple) authentication.getPrincipal();
		ResponseModel response = cabService.addCab(request, userPrincipal);
		if (response.getError().equals("false")) {
			return new ResponseEntity<ResponseModel>(response, HttpStatus.OK);
		} else {
			return new ResponseEntity<ResponseModel>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("upload-photo")
	public ResponseEntity<?> uploadCabImage(@RequestParam("cabId") Long cabId,
			@RequestParam("image") MultipartFile image) {
		return cabService.uploadCabImage(cabId, image);
	}

	@PostMapping("update-cab")
	public ResponseEntity<ResponseModel> updateCab(@RequestBody CabModel request) {
		return cabService.updateCab(request);
	}

	@PutMapping("update-cab")
	public ResponseEntity<?> updateCabDetail(Authentication authentication, @RequestBody CabModel request) {
		UserPrinciple userPrincipal = (UserPrinciple) authentication.getPrincipal();
		return cabService.updateCabDetail(request, userPrincipal.getCandidateID());
	}

	@PostMapping("inactive-cab")
	public ResponseEntity<?> inactiveCab(@RequestBody CabModel request) {
		ResponseModel response = cabService.inactiveCab(request);
		return new ResponseEntity<ResponseModel>(response, HttpStatus.OK);
	}

	@GetMapping("cab-booking-report")
	public ResponseEntity<?> cabBookingReport(@RequestParam(required = false) String cabId,
			@RequestParam(required = false) String bookingStatus) {
		return bookingService.cabBookingReport(cabId, bookingStatus);
	}

	@PutMapping("update-booking-status")
	public ResponseEntity<Map<String, Object>> updateCabStatus(@RequestBody UpdateStatusRequest request) {
		return ResponseEntity.ok(bookingService.updateCabStatus(request));
	}

	@PostMapping("uploadCabDocuments")
	public ResponseEntity<?> uploadCabDocuments(@RequestParam("seqCabId") Long seqCabId,
			@RequestParam(value = "rcDocument", required = false) MultipartFile rcDoc,
			@RequestParam(value = "insurance_document", required = false) MultipartFile insuranceDocument,
			@RequestParam(value = "cabPhoto", required = false) MultipartFile cabPhoto) {
		return cabService.uploadCabDocs(seqCabId, rcDoc, insuranceDocument, cabPhoto);
	}

	@GetMapping("get-cab/{cabRegId}")
	public ResponseEntity<Map<String, Object>>  getCab(Authentication authentication, @PathVariable String cabRegId) {
		UserPrinciple userPrincipal = (UserPrinciple) authentication.getPrincipal();
		return ResponseEntity.ok(cabService.getCab(cabRegId, userPrincipal.getCandidateID()));
	}

	@GetMapping("get-cabs")
	public ResponseEntity<?> getCabs(Authentication authentication,
			@RequestParam(value = "brand", required = false) String brand,
			@RequestParam(value = "model", required = false) String model,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate) {
		UserPrinciple userPrincipal = (UserPrinciple) authentication.getPrincipal();
		return cabService.getCabs(userPrincipal.getCandidateID(), brand, model, startDate, endDate);
	}

	@PostMapping("assign-cab")
	public ResponseEntity<?> assignCabDriver(@RequestBody AssignDriverCabModel request) {
		return cabService.assignCabDriver(request);
	}
}
