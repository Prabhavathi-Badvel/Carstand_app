package com.my.fl.startup.service;

import com.my.fl.startup.config.AWSConfig;
import com.my.fl.startup.entity.AddBike;

import com.my.fl.startup.entity.BikeMasterEntity;
import com.my.fl.startup.model.BikeMasterRequest;
import com.my.fl.startup.model.BikeModel;

import com.my.fl.startup.model.ResponseBikeModel;
import com.my.fl.startup.repo.AdminBikeMasterRepo;
import com.my.fl.startup.repo.BikeRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminBikeService {

    @Autowired
    private AdminBikeMasterRepo adminBikeMasterRepo;

    @Autowired
    private BikeRepo bikeRepo;

    @Autowired
    private AWSConfig awsConfig;

    public ResponseBikeModel addBike(BikeMasterRequest request) {
        try {
            if (request.getBrand() == null || request.getBrand().isEmpty() ||
                    request.getModel() == null || request.getModel().isEmpty() ||
                    request.getSubModel() == null || request.getSubModel().isEmpty() ||
                    request.getBodyType() == null || request.getBodyType().isEmpty()) {
                return new ResponseBikeModel("error", "Missing required fields for masterId generation.", null,
                        "Missing Fields");
            }

            String masterId = String.join("_",
                    request.getBrand(),
                    request.getModel(),
                    request.getSubModel(),
                    request.getBodyType()).replaceAll(" ", "");

            BikeMasterEntity bikeMaster = new BikeMasterEntity();
            bikeMaster.setMasterId(masterId);
            bikeMaster.setBrand(request.getBrand());
            bikeMaster.setColor(request.getColor());
            bikeMaster.setModel(request.getModel());
            bikeMaster.setTransmission(request.getTransmission());
            bikeMaster.setSubModel(request.getSubModel());
            bikeMaster.setBodyType(request.getBodyType());
            bikeMaster.setNoOfPassengers(request.getNoOfPassengers());
            bikeMaster.setFuelType(request.getFuelType());
            bikeMaster.setUpdatedBy(request.getUpdatedBy());
            bikeMaster.setUpdatedDate(LocalDateTime.now());

            bikeMaster = adminBikeMasterRepo.save(bikeMaster);

            return new ResponseBikeModel("success", "Bike master added successfully", bikeMaster, "");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseBikeModel("error", "Error adding bike master.", null, e.getMessage());
        }
    }

    public ResponseBikeModel updateBike(String masterId, BikeMasterRequest request) {
        return updateBikeField(masterId, request);
    }

    public ResponseBikeModel updateBikeField(String masterId, BikeMasterRequest request) {
        try {
            Optional<BikeMasterEntity> existingBike = adminBikeMasterRepo.findById(masterId);
            if (existingBike.isPresent()) {
                BikeMasterEntity bikeMaster = existingBike.get();
                updateBikeFieldValues(bikeMaster, request);
                bikeMaster.setUpdatedBy(request.getUpdatedBy());
                bikeMaster.setUpdatedDate(LocalDateTime.now());

                adminBikeMasterRepo.save(bikeMaster);

                return new ResponseBikeModel("success", "Bike master fields updated successfully", bikeMaster, "");
            } else {

                return new ResponseBikeModel("error", "Bike master not found with id: " + masterId, null, "Not Found");
            }
        } catch (Exception e) {

            e.printStackTrace();
            return new ResponseBikeModel("error", "Error updating bike master fields.", null, e.getMessage());
        }
    }

    private void updateBikeFieldValues(BikeMasterEntity bikeMaster, BikeMasterRequest request) {
        if (request.getBrand() != null)
            bikeMaster.setBrand(request.getBrand());
        if (request.getModel() != null)
            bikeMaster.setModel(request.getModel());
        if (request.getSubModel() != null)
            bikeMaster.setSubModel(request.getSubModel());
        if (request.getBodyType() != null)
            bikeMaster.setBodyType(request.getBodyType());
        if (request.getNoOfPassengers() != null)
            bikeMaster.setNoOfPassengers(request.getNoOfPassengers());
        if (request.getFuelType() != null)
            bikeMaster.setFuelType(request.getFuelType());
        if (request.getTransmission() != null)
            bikeMaster.setTransmission(request.getTransmission());
        if (request.getColor() != null)
            bikeMaster.setColor(request.getColor());
    }

    public Optional<BikeMasterEntity> getBikeByMasterId(String masterId) {
        return adminBikeMasterRepo.findById(masterId);
    }

    public ResponseBikeModel getBrandList() {
        try {
            List<BikeMasterEntity> bikes = adminBikeMasterRepo.findAll();
            List<String> brands = bikes.stream()
                    .map(BikeMasterEntity::getBrand)
                    .distinct()
                    .collect(Collectors.toList());

            return new ResponseBikeModel("success", "Brand list fetched successfully", brands, "");
        } catch (Exception e) {
            return new ResponseBikeModel("error", "Error occurred while fetching brand list.", null, e.getMessage());
        }
    }

    public ResponseBikeModel getBikesByBrands(List<String> brands) {
        try {
            List<BikeMasterEntity> bikeDetails = adminBikeMasterRepo.findByBrands(brands);

            if (!bikeDetails.isEmpty()) {
                return new ResponseBikeModel("success", "Bike details fetched successfully", bikeDetails, "");
            } else {
                return new ResponseBikeModel("error", "No bike details found for given brands.", null, "No Data Found");
            }
        } catch (Exception e) {
            return new ResponseBikeModel("error", "Error fetching bike details.", null, e.getMessage());
        }
    }

    public List<BikeModel> getAllBikes(String brand, String model, String bodyType, LocalDate startDate,
            LocalDate endDate,
            String userId) {
        List<AddBike> bikes = bikeRepo.findBikesByFilters(model, brand, bodyType, startDate, endDate, userId);

        if (bikes == null || bikes.isEmpty()) {
            return Collections.emptyList();
        }

        return bikes.stream()
                .map(this::mapToBikeModel)
                .collect(Collectors.toList());
    }

    private BikeModel mapToBikeModel(AddBike bike) {
        BikeModel bikeModel = new BikeModel();

        bikeModel.setBikeSeqId(bike.getBikeSeqId());
        bikeModel.setBikeRegNo(bike.getBikeRegNo());
        bikeModel.setUserId(bike.getUserId());
        bikeModel.setBikeGenId(bike.getBikeGenId());
        bikeModel.setVehicleIdNo(bike.getVehicleIdNo());

        bikeModel.setRcDoc(awsConfig.getUrl(bike.getRcDoc()));
        bikeModel.setInsuranceDoc(awsConfig.getUrl(bike.getInsuranceDoc()));
        bikeModel.setBikePhoto(awsConfig.getUrl(bike.getBikePhoto()));

        bikeModel.setBrand(bike.getBrand());
        bikeModel.setModel(bike.getModel());
        bikeModel.setModelYear(bike.getModelYear());
        bikeModel.setCurrentMileage(bike.getCurrentMileage());
        bikeModel.setFuelType(bike.getFuelType());
        bikeModel.setBodyType(bike.getBodyType());
        bikeModel.setTransmission(bike.getTransmission());
        bikeModel.setKmDriven(bike.getKmDriven());
        bikeModel.setNumberOfPassenger(bike.getNumberOfPassenger());
        bikeModel.setColor(bike.getColor());

        bikeModel.setInsuranceCompanyName(bike.getInsuranceCompanyName());
        bikeModel.setCertifiedCompanyName(bike.getCertifiedCompanyName());

        bikeModel.setRegisteredYear(bike.getRegisteredYear());
        bikeModel.setRegisteredCity(bike.getRegisteredCity());
        bikeModel.setRegisteredState(bike.getRegisteredState());

        bikeModel.setStatus(bike.getStatus());

        bikeModel.setMobileNo(bike.getMobileNo());
        bikeModel.setAddress(bike.getAddress());
        bikeModel.setCity(bike.getCity());
        bikeModel.setState(bike.getState());
        bikeModel.setDistrict(bike.getDistrict());
        bikeModel.setPincode(bike.getPincode());

        bikeModel.setBikeValidDays(bike.getBikeValidDays());

        bikeModel.setBikeAddedDate(bike.getBikeAddedDate());
        bikeModel.setBikeExpiryDate(bike.getBikeExpiryDate());

        bikeModel.setBikePlateStatus(bike.getBikePlateStatus());

        return bikeModel;
    }

}
