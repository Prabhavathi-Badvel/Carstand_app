package com.my.fl.startup.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.my.fl.startup.model.CabVerificationFeedbackModel;
import com.my.fl.startup.model.CabVerificationFeedbackRequest;
import com.my.fl.startup.model.ResponseModel;
import com.my.fl.startup.service.CabActivationService;

@RestController
@RequestMapping("/api/cabActivation/")
public class CabActivationController {

	@Autowired
	CabActivationService cabActivationService;

	@GetMapping("get-cab-details")
	// plate cabRegNo we need to implement in search
	public ResponseEntity<?> getCabDetails(@RequestParam(required = false) String userId,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate,
			@RequestParam(required = false) String cabRegNo) {
		try {
			return cabActivationService.getCabDetails(userId, startDate, endDate,cabRegNo);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return new ResponseEntity<ResponseModel>(new ResponseModel("Something Went Wrong", null),
				HttpStatus.BAD_REQUEST);
	}

	@PostMapping("verify-cab")
	public ResponseEntity<?> verifyCab(@RequestBody CabVerificationFeedbackRequest request) {
		ResponseModel response = cabActivationService.verifyCab(request);
		return new ResponseEntity<ResponseModel>(response, HttpStatus.OK);
	}

}
