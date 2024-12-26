package com.my.fl.startup.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.my.fl.startup.model.CabCancellationPolicyModel;
import com.my.fl.startup.model.ResponseModel;
import com.my.fl.startup.service.CabCancellationPolicyService;

@RestController
@RequestMapping("/api/cancellationPolicy/")
public class CabCancellationPolicyController {

	@Autowired
	CabCancellationPolicyService cabCancellationPolicyService;

	@PostMapping("booking-cancellation")
	public ResponseEntity<?> cabCancellationPolicy(@RequestBody CabCancellationPolicyModel request) {
		ResponseModel response = cabCancellationPolicyService.cabCancellationPolicy(request);
		if (response.getError().equals("false")) {
			return new ResponseEntity<ResponseModel>(response, HttpStatus.OK);
		} else {
			return new ResponseEntity<ResponseModel>(response, HttpStatus.BAD_REQUEST);
		}
	}

}
