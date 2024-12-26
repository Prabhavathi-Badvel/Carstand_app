package com.my.fl.startup.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.my.fl.startup.entity.AddCab;
import com.my.fl.startup.service.CabReportService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/cab-report")
public class CabReportController {

	@Autowired
	private CabReportService cabReportService;

	@GetMapping
	public ResponseEntity<List<AddCab>> getCabReports(@RequestParam(required = false) LocalDateTime fromRegDate,
			@RequestParam(required = false) LocalDateTime toRegDate, @RequestParam(required = false) String mobile,
			@RequestParam(required = false) String email, @RequestParam(required = false) String brand,
			@RequestParam(required = false) String status) {
		log.info(">> getUserById({})");
		return ResponseEntity.ok(cabReportService.getCabReports(fromRegDate, toRegDate, mobile, email, brand, status));
	}

}
