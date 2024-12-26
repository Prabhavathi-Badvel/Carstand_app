package com.my.fl.startup.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.my.fl.startup.entity.AdminEmailSettingsEntity;
import com.my.fl.startup.entity.AdminSMSEntity;
import com.my.fl.startup.repo.AdminEmailSettingsRepo;
import com.my.fl.startup.repo.AdminSMSSettings;
import com.my.fl.startup.utility.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.fl.startup.entity.AddCab;
import com.my.fl.startup.entity.CabVerificationFeedbackEntity;
import com.my.fl.startup.entity.MembershipPlan;
import com.my.fl.startup.entity.enums.Status;
import com.my.fl.startup.model.CabModel;
import com.my.fl.startup.model.CabVerificationFeedbackModel;
import com.my.fl.startup.model.CabVerificationFeedbackRequest;
import com.my.fl.startup.model.ResponseModel;
import com.my.fl.startup.repo.CabRepo;
import com.my.fl.startup.repo.CabVerificationFeedbackRepo;
import com.my.fl.startup.repo.MembershipPlanRepo;

@Service
public class CabActivationService {

	@Autowired
	CabRepo cabRepo;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	CabVerificationFeedbackRepo cabVerificationFeedbackRepo;

	@Autowired
	EmailService emailService;

	@Autowired
	SecurityUtils securityUtils;

	@Autowired
	AdminEmailSettingsRepo adminEmailSettingsRepo;

	@Autowired
	AdminSMSSettings adminSMSSettings;

	@Autowired
	SmsHandler smsHandler;

	@Autowired
	private MembershipPlanRepo membershipPlanRepo;

	public ResponseEntity<?> getCabDetails(String userId, LocalDate startDate, LocalDate endDate,String cabRegNo) {
		try {
			// Fetch cab data based on filters
			List<AddCab> cabData = cabRepo.findByUserIdAndCabAddedDate(userId, startDate, endDate,cabRegNo);

			// Map AddCab to CabModel and handle null fields
			List<CabModel> cabList = cabData.stream().map(cab -> {
				CabModel cb = objectMapper.convertValue(cab, CabModel.class);
				cb.setInsuranceDoc(cb.getInsuranceDoc() != null ? cb.getInsuranceDoc() : "no documents found");
				cb.setCabPhoto(cb.getCabPhoto() != null ? cb.getCabPhoto() : "no documents found");
				cb.setRcDoc(cb.getRcDoc() != null ? cb.getRcDoc() : "no documents found");
				return cb;
			}).collect(Collectors.toList());

			// Return the response with HTTP status 200
			return ResponseEntity.ok(cabList);
		} catch (Exception e) {
			// Log the exception for debugging
			e.printStackTrace();

			// Return a custom error response with HTTP status 400
			ResponseModel errorResponse = new ResponseModel("Something Went Wrong", null);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
		}
	}

	public ResponseEntity<?> getCabDetails1(String userId, String startDate, String endDate) {
		List<CabModel> modelList = new ArrayList<CabModel>();
		List<AddCab> entityList = new ArrayList<AddCab>();
		try {
			if (userId != null && startDate == null && endDate == null) {
				entityList = cabRepo.findByUserId(userId);
			} else if (userId == null && startDate != null && endDate != null) {
				entityList = cabRepo.findByDate(startDate, endDate);
			} else if (userId != null && startDate != null && endDate != null) {
				entityList = cabRepo.findByUserIdAndDate(userId, startDate, endDate);
			} else {
				entityList = cabRepo.findAll();
			}
			for (AddCab entity : entityList) {

				modelList.add(objectMapper.convertValue(entity, CabModel.class));
			}

			return new ResponseEntity<List<CabModel>>(modelList, HttpStatus.OK);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return new ResponseEntity<ResponseModel>(new ResponseModel("Something Went Wrong", null),
					HttpStatus.BAD_REQUEST);
		}
	}

	public ResponseModel verifyCab(CabVerificationFeedbackRequest request) {
		Optional<CabVerificationFeedbackEntity> exitingCab = cabVerificationFeedbackRepo
				.findByCabGenId(request.getCabGenId());
		try {
			AddCab cabEntity = cabRepo.findByCabGenId(request.getCabGenId());
			if (request.getVerificationStatus().toString().equals("VERIFIED")) {
				MembershipPlan membershipPlan = membershipPlanRepo.findByName("PLAN000").get();
				String expiryDate = LocalDate.now().plusDays(membershipPlan.getDuration())
						.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
				// LocalDate.now().plusDays(membershipPlan.getDuration()).toString();
				cabEntity.setCabExpiryDate(expiryDate);
				cabEntity.setCabValidDays(membershipPlan.getDuration() + "");
				cabEntity.setStatus(Status.ACTIVE);
				cabEntity.setCabPlateStatus("YES");
			} else {
				cabEntity.setStatus(Status.INACTIVE);
				cabEntity.setCabPlateStatus("NO");
			}
			if (exitingCab.isEmpty()) {
				CabVerificationFeedbackEntity feedBackEntity = new CabVerificationFeedbackEntity();
				feedBackEntity.setCabGenId(request.getCabGenId());
				feedBackEntity.setVerificationFeedback(request.getVerificationFeedback());
				feedBackEntity.setVerificationStatus(request.getVerificationStatus().toString());
				feedBackEntity.setAdminId(request.getAdminId());
				feedBackEntity.setVerificationDateTime(LocalDateTime.now());
				cabVerificationFeedbackRepo.save(feedBackEntity);
			}
			if (exitingCab.isPresent()) {
				exitingCab.get().setVerificationFeedback(request.getVerificationFeedback());
				exitingCab.get().setVerificationStatus(request.getVerificationStatus().toString());
				exitingCab.get().setAdminId(request.getAdminId());
				exitingCab.get().setVerificationDateTime(LocalDateTime.now());
				cabVerificationFeedbackRepo.save(exitingCab.get());
			}
			cabRepo.save(cabEntity);
			String cabRegNo = cabEntity.getCabRegNo();
			String userEmail = SecurityUtils.getLoggedInUserEmail();
			AdminEmailSettingsEntity emailDetails = adminEmailSettingsRepo.findById(4L).get();
			String formattedEmailBody = String.format(emailDetails.getEmailBody(), cabRegNo);
			emailService.sendEmailMessage(userEmail, formattedEmailBody, emailDetails.getEmailSubject());

			String Phone = securityUtils.getLoggedInUserPhoneNumber();
			AdminSMSEntity smsDetails = adminSMSSettings.findById(1L).get();
			String formattedSMS = String.format(smsDetails.getSmsMessage(), cabRegNo);
			smsHandler.sendCustomMessage(Phone, formattedSMS);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseModel("Cab status has been changed successfully. " + request.getVerificationFeedback());
	}

}
