package com.my.fl.startup.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.my.fl.startup.service.ReportService;

@RestController
@RequestMapping("/api/report")
public class ReportController {

	@Autowired
	private ReportService reportService;

	@GetMapping("/cab")
	public ResponseEntity<?> getCabReport() {
		return new ResponseEntity<>(reportService.getCabReport(), HttpStatus.OK);
	}

	@GetMapping("/register")
	public ResponseEntity<?> getRegisterUserReport() {
		return new ResponseEntity<>(reportService.getRegisterUserReport(), HttpStatus.OK);
	}
}
