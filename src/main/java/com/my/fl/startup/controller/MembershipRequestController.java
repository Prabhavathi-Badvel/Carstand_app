package com.my.fl.startup.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.my.fl.startup.entity.MembershipPlan;
import com.my.fl.startup.model.MembershipRequestModel;
import com.my.fl.startup.service.MembershipRequestService;
import com.my.fl.startup.service.UserPrinciple;

@RestController
@RequestMapping("/api/membership/")
public class MembershipRequestController {

	@Autowired
	MembershipRequestService membershipRequestService;

//	@PostMapping("add-membership-request")
//	public ResponseEntity<?> addMembershipRequest(@RequestBody List<MembershipRequestModel> request,
//			Authentication authentication) {
//		UserPrinciple userPrincipal = (UserPrinciple) authentication.getPrincipal();
//		ResponseModel response = membershipRequestService.addMembershipRequest(request, userPrincipal.getCandidateID());
//		if (response.getError().equals("false")) {
//			return new ResponseEntity<ResponseModel>(response, HttpStatus.OK);
//		} else {
//			return new ResponseEntity<ResponseModel>(response, HttpStatus.BAD_REQUEST);
//		}
//	}

	@PostMapping("add-membership-request")
	public ResponseEntity<MembershipRequestModel> addMembershipRequest(
			@RequestBody List<MembershipRequestModel> request, Authentication authentication) {
		UserPrinciple userPrincipal = (UserPrinciple) authentication.getPrincipal();
		MembershipRequestModel response = membershipRequestService.addMembership(request,
				userPrincipal.getCandidateID());
		if (response.getResponse().getError().equals("true")) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}

	@GetMapping("cab")
	public ResponseEntity<?> getCabMembership() {
		return membershipRequestService.getCabMembership();
	}

	@GetMapping("get-membership-plans")
	public ResponseEntity<?> getMembershipPlan() {
		List<MembershipPlan> response = membershipRequestService.gddMembershipPlan();
		return new ResponseEntity<List<MembershipPlan>>(response, HttpStatus.OK);
	}
}
