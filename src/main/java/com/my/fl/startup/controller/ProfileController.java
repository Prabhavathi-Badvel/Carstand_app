package com.my.fl.startup.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.my.fl.startup.model.DeleteAccountRequest;
import com.my.fl.startup.model.ResponseMessageDto;
import com.my.fl.startup.model.UpdateProfileRequest;
import com.my.fl.startup.model.UserResponse;
import com.my.fl.startup.service.ProfileService;
import com.my.fl.startup.utils.AuthDetailsProvider;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/profile")
public class ProfileController {

	@Autowired
	private ProfileService profileService;

	@GetMapping
	public ResponseEntity<UserResponse> getLoggedInUserDetail() {
		log.info(">> getUserById({})");
		String email = AuthDetailsProvider.getLoggedEmail();
		return ResponseEntity.ok(profileService.getLoggedInUserDetail(email));
	}

	@PutMapping
	public ResponseEntity<?> updateLoggedInUserDetail(
			@Valid @RequestBody UpdateProfileRequest updateProfileRequestDto) {
		String email = AuthDetailsProvider.getLoggedEmail();
		log.info(">> updateUser({}, {}, {})", email, updateProfileRequestDto);
		return new ResponseEntity<>(profileService.updateLoggedInUserDetail(email, updateProfileRequestDto),
				HttpStatus.OK);
	}

	@PostMapping("/delete-account")
	public ResponseEntity<ResponseMessageDto> servicePersonDeletAccoutn(
			@RequestBody DeleteAccountRequest accountRequest) {
		return new ResponseEntity<ResponseMessageDto>(profileService.servicePersonDeletAccout(accountRequest),
				HttpStatus.CREATED);
	}

}
