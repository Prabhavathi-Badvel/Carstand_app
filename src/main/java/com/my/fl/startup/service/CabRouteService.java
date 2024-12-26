package com.my.fl.startup.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.my.fl.startup.entity.enums.CabRouteStatus;
import com.my.fl.startup.entity.enums.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.my.fl.startup.entity.AddCab;
import com.my.fl.startup.entity.CabRouteEntity;
import com.my.fl.startup.model.CabRouteModel;
import com.my.fl.startup.model.ResponseModel;
import com.my.fl.startup.repo.CabRepo;
import com.my.fl.startup.repo.CabRouteRepo;

@Service
public class CabRouteService {

	@Autowired
	CabRouteRepo cabRouteRepo;

	@Autowired
	CabRepo cabRepo;

	public ResponseModel addCabRoute(CabRouteModel request) {

		CabRouteEntity entity = new CabRouteEntity();
		ResponseModel response = new ResponseModel();

		try {

			AddCab addCab = cabRepo.findByCabRegNo(request.getCabId());
			if (!addCab.getStatus().equals(Status.ACTIVE)) {
				response.setError("true");
				response.setMsg("Cab is not yet Verified.");
				return response;
			}
			String cabOwnerId = extractCabOwnerIdFromSecurityContext();
			String random = String.valueOf(((int) (Math.random() * (999 - 100 + 1))) + 100);
			entity.setCabModel(request.getCabModel());
			entity.setSourceAddress(request.getSourceAddress());
			entity.setDestination(request.getDestination());
			entity.setPickUpDate(request.getPickUpDate());
			entity.setFromLocation(request.getFromLocation());
			entity.setNoOfPassenger(request.getNoOfPassenger());
			entity.setPrice(request.getPrice());
			entity.setPricePerKm(request.getPricePerKm());
			entity.setCabId(request.getCabId());
			entity.setEveryDay(request.isEveryDay());
			entity.setCabOwnerId(cabOwnerId);
			entity.setStatus(CabRouteStatus.AVAILABLE);
			entity.setAvailability("YES");
			entity.setRouteGenId("ZOCB" + random);
			LocalTime localCurrentTime = LocalTime.now();
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
			String formattedTime = localCurrentTime.format(timeFormatter);
			entity.setRouteAddedTime(formattedTime);

			LocalDate localCurrentDate = LocalDate.now();
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			String formattedDate = localCurrentDate.format(dateFormatter);
			entity.setRouteAddedDate(formattedDate);

			entity.setServiceType(request.getServiceType());

			cabRouteRepo.save(entity);

			response.setError("false");
			response.setMsg("Added Successfully");
			response.setCabRouteUpdated(entity);

		} catch (Exception e) {
			response.setError("true");
			response.setMsg("Something went wrong");
			e.printStackTrace();
		}
		return response;
	}

	private String extractCabOwnerIdFromSecurityContext() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			return ((UserPrinciple) principal).getCandidateID(); // Assuming the username is the cabOwnerId
		} else {
			return principal.toString();
		}
	}

	public ResponseEntity<?> getCabRoutes(String cabId) {
		List<CabRouteModel> cabRouteModelList = new ArrayList<CabRouteModel>();
		List<CabRouteEntity> entityList = new ArrayList<CabRouteEntity>();
		try {
			String cabOwnerId = extractCabOwnerIdFromSecurityContext();
			if (cabId != null) {
				entityList = cabRouteRepo.findByOwnerIdCabId(cabOwnerId, cabId);
			} else {
				entityList = cabRouteRepo.findByOwnerId(cabOwnerId);
			}

			for (CabRouteEntity entity : entityList) {
				CabRouteModel model = new CabRouteModel();
				model.setCabRouteId(entity.getCabRouteId());
				model.setCabId(entity.getCabId());
				model.setCabModel(entity.getCabModel());
				model.setSourceAddress(entity.getSourceAddress());
				model.setDestination(entity.getDestination());
				model.setFromLocation(entity.getFromLocation());
				model.setAvailability(entity.getAvailability());
				model.setServiceType(entity.getServiceType());
				model.setNoOfPassenger(entity.getNoOfPassenger());
				model.setPrice(entity.getPrice());
				model.setPricePerKm(entity.getPricePerKm());
				model.setPickUpDate(entity.getPickUpDate());
				model.setCabOwnerId(entity.getCabOwnerId());
				model.setEveryDay(entity.isEveryDay());
				model.setStatus(entity.getStatus());

				cabRouteModelList.add(model);

			}
			return new ResponseEntity<List<CabRouteModel>>(cabRouteModelList, HttpStatus.OK);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return new ResponseEntity<ResponseModel>(
					new ResponseModel("Something went wrong while fetching cab routes", null), HttpStatus.BAD_REQUEST);
		}
	}

//	public ResponseModel updateCabRoute(CabRouteModel request) {
//		ResponseModel response = new ResponseModel();
//		try {
//			AddCab addCab = cabRepo.findBycabRegNo(request.getCabId());
//			String expiryDateString = addCab.getCabExpiryDate();
//	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//	        LocalDate expiryDate = LocalDate.parse(expiryDateString, formatter);
//			if(LocalDate.now().isBefore(expiryDate)) {
//				CabRouteEntity cabRouteEntity = cabRouteRepo.findById(request.getCabRouteId()).get();
//				cabRouteEntity.setServiceType(request.getServiceType());
//				cabRouteEntity.setSourceAddress(request.getSourceAddress());
//				cabRouteEntity.setFromLocation(request.getFromLocation());
//				cabRouteEntity.setDestination(request.getDestination());
//				cabRouteEntity.setAvailability(request.getAvailability());
//				cabRouteEntity.setNoOfPassenger(request.getNoOfPassenger());
//				cabRouteEntity.setPrice(request.getPrice());
//				cabRouteEntity.setPricePerKm(request.getPricePerKm());
//
//				cabRouteRepo.save(cabRouteEntity);
//				response.setError("false");
//	        	response.setMsg("Updated Successfully");
//			}else {
//				response.setError("true");
//	            response.setMsg("Cab is expired. Expiry Date: " + expiryDateString);
//			}
//
//
//		} catch (Exception e) {

//			response.setError("true");
//        	response.setMsg("Something went wrong");
//        	e.printStackTrace();
//		}
//		return response;
//	}
//

	@Transactional
	public ResponseModel updateCabRoute(CabRouteModel request) {

		ResponseModel response = new ResponseModel();
		try {
			AddCab addCab = cabRepo.findByCabRegNo(request.getCabId());
			if (addCab == null) {
				response.setError("true");
				response.setMsg("Cab not found");
				return response;
			}
			if (!addCab.getStatus().equals(Status.ACTIVE)) {
				response.setError("true");
				response.setMsg("Cab is not yet Verified.");
				return response;
			}

			if (addCab.getCabExpiryDate() == null) {
				response.setError("true");
				response.setMsg("Cab not verified");
				return response;
			}

			String expiryDateString = addCab.getCabExpiryDate();
			if (expiryDateString == null || expiryDateString.isEmpty()) {
				response.setError("true");
				response.setMsg("Cab expiry date not set");
				return response;
			}

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate expiryDate = LocalDate.parse(expiryDateString, formatter);

			if (LocalDate.now().isBefore(expiryDate)) {
				Optional<CabRouteEntity> cabRouteEntityOpt = cabRouteRepo.findById(request.getCabRouteId());
				if (!cabRouteEntityOpt.isPresent()) {
					response.setError("true");
					response.setMsg("Cab route not found");
					return response;
				}
				CabRouteEntity cabRouteEntity = cabRouteEntityOpt.get();
				cabRouteEntity.setServiceType(
						request.getServiceType() != null ? request.getServiceType() : cabRouteEntity.getServiceType());
				cabRouteEntity.setSourceAddress(request.getSourceAddress() != null ? request.getSourceAddress()
						: cabRouteEntity.getSourceAddress());
				cabRouteEntity.setFromLocation(request.getFromLocation() != null ? request.getFromLocation()
						: cabRouteEntity.getFromLocation());
				cabRouteEntity.setDestination(
						request.getDestination() != null ? request.getDestination() : cabRouteEntity.getDestination());
				cabRouteEntity.setAvailability(request.getAvailability() != null ? request.getAvailability()
						: cabRouteEntity.getAvailability());
				cabRouteEntity.setNoOfPassenger(request.getNoOfPassenger() != null ? request.getNoOfPassenger()
						: cabRouteEntity.getNoOfPassenger());
				cabRouteEntity.setPrice(request.getPrice() != null ? request.getPrice() : cabRouteEntity.getPrice());
				cabRouteEntity.setPricePerKm(
						request.getPricePerKm() != null ? request.getPricePerKm() : cabRouteEntity.getPricePerKm());
				cabRouteEntity
						.setStatus(request.getStatus() != null ? request.getStatus() : cabRouteEntity.getStatus());
				cabRouteEntity.setEveryDay(request.isEveryDay());

				cabRouteRepo.save(cabRouteEntity);
				response.setError("false");
				response.setMsg("Updated Successfully");
				response.setCabRouteUpdated(cabRouteEntity);

			} else {
				response.setError("true");
				response.setMsg("Cab is expired. Expiry Date: " + expiryDateString);
			}
		} catch (Exception e) {
			response.setError("true");
			response.setMsg("Something went wrong");
			e.printStackTrace();
		}
		return response;
	}

}
