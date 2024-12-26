package com.my.fl.startup.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.my.fl.startup.entity.AddCab;
import com.my.fl.startup.entity.CabMembershipRequestDetails;
import com.my.fl.startup.entity.MembershipPlan;
import com.my.fl.startup.entity.MembershipRequest;
import com.my.fl.startup.model.CabMembershipRequestDetailsModel;
import com.my.fl.startup.model.MembershipRequestModel;
import com.my.fl.startup.model.ResponseModel;
import com.my.fl.startup.repo.CabMembershipRequestDetailsRepo;
import com.my.fl.startup.repo.CabRepo;
import com.my.fl.startup.repo.MembershipPlanRepo;
import com.my.fl.startup.repo.MembershipRequestRepo;

@Service
public class MembershipRequestService {

	@Autowired
	MembershipRequestRepo membershipRequestRepo;

	@Autowired
	MembershipPlanRepo membershipPlanRepo;

	@Autowired
	CabMembershipRequestDetailsRepo cabMembershipRequestDetailsRepo;

	@Autowired
	CabRepo cabRepo;

	public ResponseModel addMembershipRequest(List<MembershipRequestModel> request, String ownerId) {
		ResponseModel response = new ResponseModel();

		for (MembershipRequestModel obj : request) {
			if (obj.getCabMembershipRequestDetailsModel() == null
					|| obj.getCabMembershipRequestDetailsModel().getMembershipRequestId() == null) {
				response.setError("true");
				response.setMsg("Missing Membership Request Id");
				return response;
			}
			AddCab cab = cabRepo.findByCabGenIdAndCabOwnerId(
					obj.getCabMembershipRequestDetailsModel().getMembershipRequestId(), ownerId);
			if (cab == null) {
				response.setError("true");
				response.setMsg("Invalid Membership Request Id");
				return response;
			}

			MembershipPlan membershipEntity = membershipPlanRepo
					.findById(obj.getCabMembershipRequestDetailsModel().getMembershipPlanId()).orElse(null);
			if (membershipEntity == null) {
				response.setError("true");
				response.setMsg("Invalid Membership Plan Id");
				return response;
			}
		}

		try {
			for (MembershipRequestModel obj : request) {
				MembershipRequest entity = new MembershipRequest();
				entity.setPhonePayNumber(obj.getPhonePayNumber());
				entity.setPurchaseDate(obj.getPurchaseDate());
				entity.setUpdatedDate(obj.getUpdatedDate());
				entity.setStatus("inactive");
				entity.setTotalAmount(obj.getTotalAmount());
				entity.setTransactionNo(obj.getTransactionNo());
				entity = membershipRequestRepo.save(entity);

				MembershipPlan membershipEntity = membershipPlanRepo
						.findById(obj.getCabMembershipRequestDetailsModel().getMembershipPlanId()).get();

				CabMembershipRequestDetails detail = new CabMembershipRequestDetails();
				detail.setAddCabId(obj.getCabMembershipRequestDetailsModel().getAddCabId());
				detail.setMembershipPlanId(obj.getCabMembershipRequestDetailsModel().getMembershipPlanId());
				detail.setMembershipRequestId(entity.getId());
				detail.setPlanDuration(membershipEntity.getDuration()+"");

				cabMembershipRequestDetailsRepo.save(detail);
			}
			response.setError("false");
			response.setMsg("Added Successfully");

		} catch (Exception ex) {
			response.setError("true");
			response.setMsg("Something went wrong");
			ex.printStackTrace();
		}

		return response;
	}

	public MembershipRequestModel addMembership(List<MembershipRequestModel> request, String ownerId) {
		MembershipRequestModel responseModel = new MembershipRequestModel();
		AddCab cab = null;
		for (MembershipRequestModel obj : request) {
			if (obj.getCabMembershipRequestDetailsModel() == null
					|| obj.getCabMembershipRequestDetailsModel().getMembershipRequestId() == null) {
				responseModel.setResponse(new ResponseModel("true", "Missing Membership Request Id"));
				return responseModel;
			}
			cab = cabRepo.findByCabGenIdAndCabOwnerId(
					obj.getCabMembershipRequestDetailsModel().getMembershipRequestId(), ownerId);
			if (cab == null) {
				responseModel.setResponse(new ResponseModel("true", "Invalid Membership Request Id"));
				return responseModel;
			}

			MembershipPlan membershipEntity = membershipPlanRepo
					.findById(obj.getCabMembershipRequestDetailsModel().getMembershipPlanId()).orElse(null);
			if (membershipEntity == null) {
				responseModel.setResponse(new ResponseModel("true", "Invalid Membership Plan Id"));
				return responseModel;
			}
		}

		try {
			int count = 1;
			for (MembershipRequestModel obj : request) {
				MembershipRequest entity = new MembershipRequest();
				entity.setPhonePayNumber(obj.getPhonePayNumber());
				entity.setMembershipRequestId("MM" + System.currentTimeMillis() + OTPService.generateOTP(true));
				entity.setPurchaseDate(LocalDateTime.now());
				entity.setUpdatedDate(obj.getUpdatedDate());
				entity.setStatus("inactive");
				entity.setTotalAmount(obj.getTotalAmount());
				entity.setTransactionNo(obj.getTransactionNo());
				entity = membershipRequestRepo.save(entity);

				MembershipPlan membershipEntity = membershipPlanRepo
						.findById(obj.getCabMembershipRequestDetailsModel().getMembershipPlanId()).get();

				CabMembershipRequestDetails detail = new CabMembershipRequestDetails();
				detail.setAddCabId(obj.getCabMembershipRequestDetailsModel().getAddCabId());
				detail.setMembershipPlanId(obj.getCabMembershipRequestDetailsModel().getMembershipPlanId());
				detail.setMembershipRequestId(entity.getId());
				detail.setMembershipRequestLineId(entity.getMembershipRequestId() + "_000" + count);
				detail.setPlanDuration(membershipEntity.getDuration()+"");
				detail.setAddCabId(cab.getCabSeqId());
				detail.setPlanExpiryDate(obj.getCabMembershipRequestDetailsModel().getPlanExpiryDate());
				cabMembershipRequestDetailsRepo.save(detail);
				count++;
			}
			responseModel.setResponse(new ResponseModel("false", "Added Successfully"));
		} catch (Exception ex) {
			responseModel.setResponse(new ResponseModel("true", "Something went wrong"));
			ex.printStackTrace();
		}

		return responseModel;
	}

	public ResponseEntity<?> getCabMembership() {

		List<CabMembershipRequestDetailsModel> response = new ArrayList<CabMembershipRequestDetailsModel>();
		List<CabMembershipRequestDetails> details = cabMembershipRequestDetailsRepo.findAll();
		for (CabMembershipRequestDetails obj : details) {
			CabMembershipRequestDetailsModel member = new CabMembershipRequestDetailsModel();
			Optional<MembershipRequest> entity = membershipRequestRepo.findById(obj.getMembershipRequestId());
			if (entity.isPresent()) {
				member.setPurchaseDate(entity.get().getPurchaseDate().toString());
			}
			member.setAddCabId(obj.getAddCabId());
			member.setMembershipPlanId(obj.getMembershipPlanId());
			member.setMembershipRequestId(obj.getMembershipRequestId().toString());
			member.setPlanDuration(obj.getPlanDuration());
			member.setPlanExpiryDate(obj.getPlanExpiryDate());
			member.setId(obj.getId());
			response.add(member);
		}

		return new ResponseEntity<List<CabMembershipRequestDetailsModel>>(response, HttpStatus.OK);
	}

	public List<MembershipPlan> gddMembershipPlan() {
		return membershipPlanRepo.findAll();
	}

}
