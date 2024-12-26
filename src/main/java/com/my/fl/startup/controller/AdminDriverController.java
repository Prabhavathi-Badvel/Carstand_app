package com.my.fl.startup.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.my.fl.startup.entity.enums.VericationStatus;
import com.my.fl.startup.model.ResponseModel;
import com.my.fl.startup.service.AdminDriverService;

@RestController
@RequestMapping("/api/admin/driver")
public class AdminDriverController {

	@Autowired
	private AdminDriverService adminDriverService;

	@PostMapping("/verify")
	public ResponseEntity<?> verifyDriver(@RequestParam String driverId, @RequestParam VericationStatus status) {
		ResponseModel response = adminDriverService.verifyDriver(driverId, status);
		return response.getError().equals("false") ? new ResponseEntity<>(response, HttpStatus.OK)
				: new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

}
