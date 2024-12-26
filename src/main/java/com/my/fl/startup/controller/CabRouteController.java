package com.my.fl.startup.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.my.fl.startup.model.CabRouteModel;
import com.my.fl.startup.model.ResponseModel;
import com.my.fl.startup.service.CabRouteService;

@RestController
@RequestMapping("/api/cabRoute")
public class CabRouteController {
	
	@Autowired
	CabRouteService cabRouteService;
	
	@PostMapping("/add-cab-route")
	public ResponseEntity<ResponseModel> addCabRoute(@RequestBody CabRouteModel request){
		
		ResponseModel response = cabRouteService.addCabRoute(request);
		if(response.getError().equals("false")) {
			return new ResponseEntity<>(response, HttpStatus.OK);
		}else {
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
	}
	@GetMapping("/get-cab-routes")
	public ResponseEntity<?> getCabRoutes(@RequestParam(required = false) String cabId){
		try {
			return cabRouteService.getCabRoutes(cabId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>("Something went wrong while fetching cab routes", HttpStatus.BAD_REQUEST);
	}
	
	@PutMapping("/update-cab-route")
	public ResponseEntity<ResponseModel> updateCabRoute(@RequestBody CabRouteModel request){
		ResponseModel response = cabRouteService.updateCabRoute(request);
		if("false".equals(response.getError())) {
			return new ResponseEntity<>(response, HttpStatus.OK);
		}else {
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
	}

}
