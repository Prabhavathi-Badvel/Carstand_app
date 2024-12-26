package com.my.fl.startup.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.fl.startup.entity.AddCab;
import com.my.fl.startup.entity.CabMasterEntity;
import com.my.fl.startup.model.CabMasterRequest;
import com.my.fl.startup.model.CabModel;
import com.my.fl.startup.model.ResponseModel;
import com.my.fl.startup.repo.AdminCabMasterRepo;
import com.my.fl.startup.repo.CabRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminCabService {

	@Autowired
	private AdminCabMasterRepo adminCabMasterRepo;

	@Autowired
	private CabRepo cabRepo;

	@Autowired
	private ObjectMapper objectMapper;

	public ResponseModel adminAddCab(CabMasterRequest request) {
		ResponseModel responseModel = new ResponseModel();

		try {
			CabMasterEntity cabMaster = new CabMasterEntity();
			cabMaster.setMasterId(request.getMasterId());
			cabMaster.setBrand(request.getBrand());
			cabMaster.setModel(request.getModel());
			cabMaster.setSubModel(request.getSubModel());
			cabMaster.setBodyType(request.getBodyType());
			cabMaster.setNoOfPassengers(request.getNoOfPassengers());
			cabMaster.setFuelType(request.getFuelType());

			cabMaster = adminCabMasterRepo.save(cabMaster); // Save and get the saved entity

			responseModel.setError("false");
			responseModel.setMsg("Cab master added successfully");
			responseModel.setCabMaster(cabMaster); // Use setData to set the entity in the response

		} catch (Exception e) {
			responseModel.setError("true");
			responseModel.setMsg("Error adding cab master: " + e.getMessage());
		}
		return responseModel;
	}

	public List<CabModel> getAllCabs(String brand, String model, String bodytype, LocalDate startDate, LocalDate endDate,
			String userId) {
		List<AddCab> cabs = cabRepo.findByCabModelAndCabBrandAndBodyTypeAndCabAddedDateAndCabOwnerId(model, brand, bodytype,
				startDate, endDate,userId);
		return cabs.stream().map(cab -> {
			return objectMapper.convertValue(cab, CabModel.class);
		}).toList();
	}

	public Optional<CabMasterEntity> getCabById(String id) {
		return adminCabMasterRepo.findById(id);
	}

	public ResponseModel updateCab(String id, CabMasterRequest request) {
		ResponseModel responseModel = new ResponseModel();

		Optional<CabMasterEntity> existingCab = adminCabMasterRepo.findById(id);
		if (existingCab.isPresent()) {
			CabMasterEntity cabMaster = existingCab.get();

			cabMaster.setBrand(request.getBrand() == null ? cabMaster.getBrand() : request.getBrand());
			cabMaster.setModel(request.getModel() == null ? cabMaster.getModel() : request.getModel());
			cabMaster.setSubModel(request.getSubModel() == null ? cabMaster.getSubModel() : request.getSubModel());
			cabMaster.setBodyType(request.getBodyType() == null ? cabMaster.getBodyType() : request.getBodyType());
			cabMaster.setNoOfPassengers(
					request.getNoOfPassengers() == null ? cabMaster.getNoOfPassengers() : request.getNoOfPassengers());
			cabMaster.setFuelType(request.getFuelType() == null ? cabMaster.getFuelType() : request.getFuelType());
			cabMaster.setTransmission(
					request.getTransmission() == null ? cabMaster.getTransmission() : request.getTransmission());
			cabMaster.setColor(request.getColor() == null ? cabMaster.getColor() : request.getColor());
			cabMaster.setUpdatedBy(request.getUpdatedBy());
			cabMaster.setUpdatedDate(LocalDateTime.now());

			adminCabMasterRepo.save(cabMaster);
			responseModel.setError("false");
			responseModel.setMsg("Cab master updated successfully");
			responseModel.setCabMaster(cabMaster);
		} else {
			responseModel.setError("true");
			responseModel.setMsg("Cab master not found with id: " + id);
		}

		return responseModel;
	}

	public ResponseModel updateCabField(String id, CabMasterRequest request) {

		ResponseModel responseModel = new ResponseModel();

		Optional<CabMasterEntity> existingCab = adminCabMasterRepo.findById(id);
		if (existingCab.isPresent()) {
			CabMasterEntity cabMaster = existingCab.get();

			cabMaster.setBrand(request.getBrand() == null ? cabMaster.getBrand() : request.getBrand());
			cabMaster.setModel(request.getModel() == null ? cabMaster.getModel() : request.getModel());
			cabMaster.setSubModel(request.getSubModel() == null ? cabMaster.getSubModel() : request.getSubModel());
			cabMaster.setBodyType(request.getBodyType() == null ? cabMaster.getBodyType() : request.getBodyType());
			cabMaster.setNoOfPassengers(
					request.getNoOfPassengers() == null ? cabMaster.getNoOfPassengers() : request.getNoOfPassengers());
			cabMaster.setFuelType(request.getFuelType() == null ? cabMaster.getFuelType() : request.getFuelType());
			cabMaster.setTransmission(
					request.getTransmission() == null ? cabMaster.getTransmission() : request.getTransmission());
			cabMaster.setColor(request.getColor() == null ? cabMaster.getColor() : request.getColor());
			cabMaster.setUpdatedBy(request.getUpdatedBy());
			cabMaster.setUpdatedDate(LocalDateTime.now());

			adminCabMasterRepo.save(cabMaster);
			responseModel.setError("false");
			responseModel.setMsg("Cab master updated successfully");
			responseModel.setCabMaster(cabMaster);
		} else {
			responseModel.setError("true");
			responseModel.setMsg("Cab master not found with id: " + id);
		}

		return responseModel;
	}

	public ResponseModel getBrandList() {
		ResponseModel responseModel = new ResponseModel();
		List<CabMasterEntity> existingCabs = adminCabMasterRepo.findAll();
		List<String> brands = existingCabs.stream().map(CabMasterEntity::getBrand).collect(Collectors.toList());

		if (!brands.isEmpty()) {
			responseModel.setMsg("Brand List Fetched Successfully");
			responseModel.setBrands(brands);
			responseModel.setError("false");
		} else {
			responseModel.setMsg("Unable to fetch Brands");
			responseModel.setError("true");
		}

		return responseModel;
	}

	public ResponseModel getCabByBrands(List<String> brands) {

		ResponseModel responseModel = new ResponseModel();

		List<CabMasterEntity> cabDetails = brands.stream().map(adminCabMasterRepo::findByBrand).filter(Objects::nonNull)
				.collect(Collectors.toList());
		if (!cabDetails.isEmpty()) {
			responseModel.setMsg("Brand Details Fetched Successfully.");
			responseModel.setCabMasterEntities(cabDetails);
			responseModel.setError("false");
		} else {
			responseModel.setMsg("No Cab Details Found for Given Brands.");
			responseModel.setError("True");
		}

		return responseModel;
	}

}
