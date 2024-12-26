package com.my.fl.startup.controller;

import com.my.fl.startup.entity.CabMasterEntity;
import com.my.fl.startup.model.AdminStateDistrictModel;
import com.my.fl.startup.model.AdminStateDistrictResponseModel;
import com.my.fl.startup.model.CabMasterRequest;
import com.my.fl.startup.model.CabModel;
import com.my.fl.startup.model.ResponseModel;
import com.my.fl.startup.service.AdminCabService;
import com.my.fl.startup.service.AdminStateDistrictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/cab/")
public class AdminCabController {

	@Autowired
	private AdminCabService adminCabService;

	@Autowired
	private AdminStateDistrictService adminStateDistService;

	@PostMapping("addAdminCab")
	public ResponseEntity<?> saveAdminCab(@RequestBody CabMasterRequest request) {
		ResponseModel response = adminCabService.adminAddCab(request);
		return response.getError().equals("false") ? new ResponseEntity<>(response.getMsg(), HttpStatus.OK)
				: new ResponseEntity<>(response.getMsg(), HttpStatus.BAD_REQUEST);
	}

	@PutMapping("updateAdminCab/{id}")
	public ResponseEntity<?> updateAdminCab(@PathVariable String id, @RequestBody CabMasterRequest request) {
		ResponseModel response = adminCabService.updateCab(id, request);
		return response.getError().equals("false") ? new ResponseEntity<>(response.getMsg(), HttpStatus.OK)
				: new ResponseEntity<>(response.getMsg(), HttpStatus.BAD_REQUEST);
	}

	@PatchMapping("updateAdminCab/{id}")
	public ResponseEntity<?> updateAdminCabField(@PathVariable String id, @RequestBody CabMasterRequest request) {
		ResponseModel response = adminCabService.updateCabField(id, request);
		return response.getError().equals("false") ? new ResponseEntity<>(response.getMsg(), HttpStatus.OK)
				: new ResponseEntity<>(response.getMsg(), HttpStatus.BAD_REQUEST);
	}

	@GetMapping("getAllCabs")
	public List<CabModel> getAllCabs(@RequestParam(value = "brand", required = false) String brand,
			@RequestParam(value = "model", required = false) String model,
			@RequestParam(value = "bodyType", required = false) String bodyType,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate,
			@RequestParam(required = false) String userId) {
		return adminCabService.getAllCabs(brand, model,bodyType, startDate, endDate, userId);
	}

	@GetMapping("getCabById/{id}")
	public ResponseEntity<?> getCabById(@PathVariable String id) {
		Optional<CabMasterEntity> cabMaster = adminCabService.getCabById(id);
		return cabMaster.map(cab -> new ResponseEntity<>(cab, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("getCabByBrand")
	public ResponseEntity<?> getCabByBrands(@RequestParam List<String> brands) {

		ResponseModel response = adminCabService.getCabByBrands(brands);
		return (response.getError().equalsIgnoreCase("false") && !response.getCabMasterEntities().contains(null))
				? new ResponseEntity<>(response, HttpStatus.OK)
				: new ResponseEntity<>(response.getMsg(), HttpStatus.BAD_REQUEST);
	}

	@GetMapping("/getBrandList")
	public ResponseEntity<?> getBrandList() {
		ResponseModel response = adminCabService.getBrandList();
		return response.getError().equals("false") ? new ResponseEntity<>(response, HttpStatus.OK)
				: new ResponseEntity<>(response.getMsg(), HttpStatus.BAD_REQUEST);
	}

	@PostMapping("/addStateDist")
	public ResponseEntity<?> addStateDist(@RequestBody AdminStateDistrictModel adminStateDist) throws Exception {
		AdminStateDistrictResponseModel createdStateDist = adminStateDistService.addStateDist(adminStateDist);
		return createdStateDist.getError().equals("false") ? new ResponseEntity<>(createdStateDist, HttpStatus.CREATED)
				: new ResponseEntity<>(createdStateDist.getMsg(), HttpStatus.BAD_REQUEST);
	}

	@GetMapping("/stateDist/{id}")
	public ResponseEntity<?> getStateDistById(@PathVariable String id) {
		return adminStateDistService.getStateDistById(id)
				.map(stateDist -> new ResponseEntity<>(stateDist, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/getAllAdminStateDistrictDetails")
	public ResponseEntity<?> getAllAdminStateDist() {
		AdminStateDistrictResponseModel response = adminStateDistService.findAllAdminStateDist();
		return response.getError().equals("false") ? new ResponseEntity<>(response, HttpStatus.OK)
				: new ResponseEntity<>(response.getMsg(), HttpStatus.BAD_REQUEST);
	}

	@PatchMapping("/updateAdminStateDistrictDetail/{id}")
	public ResponseEntity<?> updateAdminStateDistrictStatusById(@PathVariable String id,
			@RequestBody AdminStateDistrictModel adminStateDist) {
		AdminStateDistrictResponseModel createdStateDist = adminStateDistService.updateAdminStateDistrictStatusById(id,
				adminStateDist);
		return createdStateDist.getError().equals("false") ? new ResponseEntity<>(createdStateDist, HttpStatus.OK)
				: new ResponseEntity<>(createdStateDist.getMsg(), HttpStatus.BAD_REQUEST);
	}

	@PatchMapping("/updateAdminStateDistrictDetail")
	public ResponseEntity<?> updateAdminStateDistrictStatus(@RequestBody AdminStateDistrictModel adminStateDist) {
		AdminStateDistrictResponseModel createdStateDist = adminStateDistService
				.updateAdminStateDistrictStatus(adminStateDist);
		return createdStateDist.getError().equals("false") ? new ResponseEntity<>(createdStateDist, HttpStatus.OK)
				: new ResponseEntity<>(createdStateDist.getMsg(), HttpStatus.BAD_REQUEST);
	}

}
