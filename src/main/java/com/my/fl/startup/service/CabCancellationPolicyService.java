package com.my.fl.startup.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.my.fl.startup.entity.CabCancellationPolicy;
import com.my.fl.startup.model.CabCancellationPolicyModel;
import com.my.fl.startup.model.ResponseModel;
import com.my.fl.startup.repo.CabCancellationPolicyRepo;

@Service
public class CabCancellationPolicyService {
	
	@Autowired
	CabCancellationPolicyRepo cabCancellationPolicyRepo;
	
	public ResponseModel cabCancellationPolicy(CabCancellationPolicyModel request) {
		CabCancellationPolicy entity = new CabCancellationPolicy();
		ResponseModel response = new ResponseModel();
		try {
			String cabOwnerId = extractCabOwnerIdFromSecurityContext();
			entity.setCabRegNo(request.getCabRegNo());
			entity.setTime(request.getTime());
			entity.setCancellationCharges(request.getCancellationCharges());
			entity.setCancellationChargesStatus(request.getCancellationChargesStatus());
			
			LocalDate localCurrentDate = LocalDate.now();
	        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        	String formattedDate = localCurrentDate.format(dateFormatter);
        	entity.setChargesAddedDate(formattedDate);
        	
        	entity.setCabOwnerId(cabOwnerId);
        	
        	cabCancellationPolicyRepo.save(entity);
        	
        	response.setError("false");
        	response.setMsg("Added Successfully");
			response.setCabCancellationPolicy(entity);
			
		} catch (Exception e) {
			// TODO: handle exception
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



}
