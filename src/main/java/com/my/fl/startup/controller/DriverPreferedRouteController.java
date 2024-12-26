package com.my.fl.startup.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.my.fl.startup.model.DriverPreferedRouteModel;
import com.my.fl.startup.model.DriverPreferedRouteRequest;
import com.my.fl.startup.model.PaginatedResponse;
import com.my.fl.startup.model.ResponseModel;
import com.my.fl.startup.service.DriverPreferedRouteService;

@RestController
@RequestMapping("/api/driverPreferedRoute/")
public class DriverPreferedRouteController {

	@Autowired
	DriverPreferedRouteService driverPreferedRouteService;

	@PostMapping("add-driver-route")
	public ResponseEntity<ResponseModel> addDriverPreferedRoute(
			@RequestBody DriverPreferedRouteRequest driverPreferedRouteModel) {

		ResponseModel response = driverPreferedRouteService.addDriverRoute(driverPreferedRouteModel);

		if (response.getError().equals("false")) {
			return new ResponseEntity<ResponseModel>(response, HttpStatus.OK);
		} else {
			return new ResponseEntity<ResponseModel>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/get-driver-route")
	public PaginatedResponse<DriverPreferedRouteModel> getDriverRoute(
			@RequestParam(value = "fromCity", required = false) String fromCity,
			@RequestParam(value = "toCity", required = false) String toCity,
			@RequestParam(value = "mUserId", required = false) String mUserId,
			@RequestParam(value = "driverId", required = false) List<String> driverId,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "pageNo", required = false) Integer pageNo) {

		Integer parsePageSize = pageSize != null ? pageSize : 10;
		Integer parsePageNo = pageNo != null ? pageNo : 0;

		return driverPreferedRouteService.getDriverPreferedRoute(parsePageNo, parsePageSize, mUserId, fromCity, toCity,
				driverId);

	}

	@PutMapping("update-driver-route")
	public ResponseEntity<ResponseModel> updateDriverPreferedRoute(
			@RequestBody DriverPreferedRouteModel driverPreferedRouteModel) {

		ResponseModel response = driverPreferedRouteService.updateDriverRoute(driverPreferedRouteModel);

		if (response.getError().equals("false")) {
			return new ResponseEntity<ResponseModel>(response, HttpStatus.OK);
		} else {
			return new ResponseEntity<ResponseModel>(response, HttpStatus.BAD_REQUEST);
		}
	}

}