package com.my.fl.startup.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.my.fl.startup.entity.DeleteUser;
import com.my.fl.startup.entity.RegistrationEntity;
import com.my.fl.startup.entity.Role;
import com.my.fl.startup.entity.UserOTPEntity;
import com.my.fl.startup.entity.UserRoleEntity;
import com.my.fl.startup.exception.ResourceNotFoundException;
import com.my.fl.startup.model.DeleteAccountRequest;
import com.my.fl.startup.model.ResponseMessageDto;
import com.my.fl.startup.model.UpdateProfileRequest;
import com.my.fl.startup.model.UserResponse;
import com.my.fl.startup.repo.DeleteUserRepo;
import com.my.fl.startup.repo.RegistrationRepository;
import com.my.fl.startup.repo.RoleRepository;
import com.my.fl.startup.repo.UserOTPRepository;
import com.my.fl.startup.repo.UserRoleRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProfileService {

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private RegistrationRepository registrationRepository;

	@Autowired
	private DeleteUserRepo deleteUserRepo;

	@Autowired
	private UserOTPRepository otpRepository;

	@Autowired
	private UserRoleRepository roleRepository;

	@Transactional
	public UserResponse getLoggedInUserDetail(String email) {
		log.info(">>getUserById({})", email);
		RegistrationEntity user = registrationRepository.findByEmail(email);
		if (user == null) {
			throw new ResourceNotFoundException("User Not Found By : " + email);
		}
		UserResponse userResp = mapper.map(user, UserResponse.class);
		String role = user.getRoles().stream().map(r -> r.getName().toString()).collect(Collectors.joining(","));
		userResp.setRoles(role);
		return userResp;
	}

	@Transactional
	public Object updateLoggedInUserDetail(String email, UpdateProfileRequest updateProfileRequest) {
		log.info(">>updateUser({}, {})", email, updateProfileRequest);
		RegistrationEntity user = registrationRepository.findByEmail(email);
		if (user == null) {
			throw new ResourceNotFoundException("User Not Found By : " + email);
		}
		user.setFirstName(updateProfileRequest.getFirstName());
		user.setAddress(updateProfileRequest.getAddress());
		UserResponse userResp = mapper.map(user, UserResponse.class);
		String role = user.getRoles().stream().map(r -> r.getName().toString()).collect(Collectors.joining(","));
		userResp.setRoles(role);
		return userResp;
	}

	@Transactional
	public ResponseMessageDto servicePersonDeletAccout(DeleteAccountRequest accountRequest) {
		ResponseMessageDto rm = new ResponseMessageDto();
		Optional<RegistrationEntity> user = registrationRepository.findByCandidateID(accountRequest.getUserId());
		Optional<UserOTPEntity> otpTable = otpRepository.findByUserPhoneNumberAndUserEmail(user.get().getMobileNumber(),
				user.get().getEmail());
		if (user.isEmpty()) {
			rm.setMessage("Account not found.!");
			rm.setStatus(false);
			return rm;
		}
		DeleteUser deleteUser = new DeleteUser();
		deleteUser.setDeletedDate(LocalDateTime.now());
		deleteUser.setDeleteReason(accountRequest.getReason());
		deleteUser.setEmail(user.get().getEmail());
		deleteUser.setPhone(user.get().getMobileNumber());
		deleteUser.setDeactivated(true);
		deleteUser.setCandidateId(user.get().getCandidateID());
		deleteUserRepo.save(deleteUser);
		rm.setMessage("Account deleted. Thank you for being with us.");
		rm.setStatus(true);
		registrationRepository.delete(user.get());
		otpRepository.delete(otpTable.get());
		List<Long> ids = user.get().getRoles().stream().map(Role::getId).collect(Collectors.toList());
		List<UserRoleEntity> userRoleEntity = roleRepository.findByRoleIdAndRegId(ids, user.get().getId());
		roleRepository.deleteAll(userRoleEntity);
		return rm;

	}

}
