package com.my.fl.startup.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.my.fl.startup.constants.ZOROConstants;
import com.my.fl.startup.entity.RegistrationEntity;
import com.my.fl.startup.entity.Role;
import com.my.fl.startup.entity.RoleName;
import com.my.fl.startup.entity.UserOTPEntity;
import com.my.fl.startup.jwt.SignUpForm;
import com.my.fl.startup.model.OTPRequest;
import com.my.fl.startup.model.ResponseMessageDto;
import com.my.fl.startup.model.VerifyOTPRequest;
import com.my.fl.startup.repo.RegistrationRepository;
import com.my.fl.startup.repo.RoleRepository;
import com.my.fl.startup.repo.UserOTPRepository;

@Service
public class UserService {

	@Autowired
	private RegistrationRepository registrationRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserOTPRepository userOTPRepository;

	@Autowired
	private OTPService otpService;

	
	@Transactional
	public ResponseEntity<ResponseMessageDto> addUser(SignUpForm signUpRequest) {
		ResponseMessageDto response = new ResponseMessageDto();
		if (registrationRepository.existsByEmail(signUpRequest.getEmail())) {
			response.setMessage("Error: Email is already in used!");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		if (registrationRepository.existsByMobileNumber(signUpRequest.getMobileNumber())) {
			response.setMessage("Error: Mobile is already in used!");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		RegistrationEntity registration = createRegistrationEntity(signUpRequest);
		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(RoleName.ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(adminRole);

					break;
				case "business_admin":
					Role businessAdminRole = roleRepository.findByName(RoleName.BUSINESS_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(businessAdminRole);

					break;
				case "traveller":
					Role travellerRole = roleRepository.findByName(RoleName.TRAVELLER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(travellerRole);

					break;
				default:
					Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(userRole);
				}
			});
		}

		registration.setRoles(roles);
		if (registrationRepository.save(registration) != null) {
			boolean isOTPSend = otpService.createUserOTP(registration.getCandidateID(), registration.getEmail(),
					registration.getMobileNumber());
			if (isOTPSend) {
				response.setMessage(
						"Thanks for registering with us. Please verify your registered email and mobile number before login");
				response.setStatus(true);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		}
		response.setMessage("User registration failed.");
		response.setStatus(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private RegistrationEntity createRegistrationEntity(SignUpForm signUpRequest) {
		RegistrationEntity registrationEntity = new RegistrationEntity();
		registrationEntity.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
		registrationEntity.setAddress(signUpRequest.getAddress());
		registrationEntity.setEmail(signUpRequest.getEmail());
		registrationEntity.setMobileNumber(signUpRequest.getMobileNumber());
		registrationEntity.setCandidateID("ZO" + Instant.now().toString().replaceAll("[^a-zA-Z0-9]", ""));
		registrationEntity.setFirstName(signUpRequest.getName());
		registrationEntity.setRegDate(LocalDateTime.now());
		registrationEntity.setUserType("C");
		registrationEntity.setStatus("INACTIVE");
		return registrationEntity;
	}

	public boolean sendEmailOtp(OTPRequest request) {
		RegistrationEntity userOptional = registrationRepository.findByEmail(request.getEmail());
		if (null != userOptional) {
			String userEmail = request.getEmail();
			return (request.getForgetPassword() ? otpService.sendOtpToEmail(userEmail, true)
					: otpService.sendOtpToEmail(userEmail, false));
		}
		return false;
	}

	public boolean sendOtpToSMS(OTPRequest request) {
		RegistrationEntity user = registrationRepository.findByMobileNumber(request.getPhoneNumber());
		if (null != user) {
			String userPhoneNumber = request.getPhoneNumber();
			return otpService.sendOtpToPhoneNumber(userPhoneNumber);
		}
		return false;
	}

	public boolean verifyEmailOtp(VerifyOTPRequest verifyOTPRequest) throws Exception {
		String email = verifyOTPRequest.getEmail();
		String emailOtp = verifyOTPRequest.getOtp();
		RegistrationEntity user = registrationRepository.findByEmail(email);
		if (user == null) {
			throw new UsernameNotFoundException("Please Check Your Email.");
		}

		UserOTPEntity userOtpEntity = userOTPRepository.findByUserEmailAndEmailOtpOrForgetEmailOtp(email, emailOtp);

		if (userOtpEntity == null) {
			return false;
		}
		if (verifyOTPRequest.getIsForgetPassword()) {
			return userOtpEntity.getActive() != null && userOtpEntity.getActive() == 1;
		} else {
			if (userOtpEntity.isEmailVerify() || userOtpEntity.getActive() == 1) {
				ZOROConstants.IS_EMAIL_ALREADY_VERIFIED = true;
			}
			if (!userOtpEntity.isEmailVerify()) {
				user.setStatus("ACTIVE");
				userOtpEntity.setEmailVerify(true);
				userOtpEntity.setActive(1);
				userOtpEntity.setUpdatedOn(LocalTime.now().toString());
				userOTPRepository.save(userOtpEntity);
				registrationRepository.updateStatusByEmail("ACTIVE", email);
				return true;
			}
		}

		return false;
	}

	public boolean verifySmsOTP(VerifyOTPRequest smsOtp) {
		String phoneOtp = smsOtp.getOtp();
		String phoneNumber = smsOtp.getPhoneNumber();
		RegistrationEntity user = registrationRepository.findByMobileNumber(phoneNumber);
		if (user == null) {
			throw new UsernameNotFoundException("Please Check Your Mobile Number.");
		}

		UserOTPEntity userOtpEntity = userOTPRepository.findByUserPhoneNumberAndPhoneOtpOrForgetEmailOtp(phoneNumber,
				phoneOtp);

		if (userOtpEntity != null) {
			if (smsOtp.getIsForgetPassword()) {
				return userOtpEntity.getActive() != null && userOtpEntity.getActive() == 1;
			} else {
				if (userOtpEntity.getActive() == 1) {
					ZOROConstants.IS_EMAIL_ALREADY_VERIFIED = true;
				}
				userOtpEntity.setSmsVerify(true);
				userOtpEntity.setActive(1);
				userOtpEntity.setUpdatedOn(LocalTime.now().toString());
				if (!user.getStatus().equalsIgnoreCase("ACTIVE")) {
					registrationRepository.updateStatusByMobileNumber("ACTIVE", phoneNumber);
				}
				userOTPRepository.save(userOtpEntity);
				return true;
			}
		}
		return false;
	}

	public String resetPassword(String emailOrPhone, String newPassword, String otp, boolean isForgetPassword) {
		if (!otpService.verifyOtp(emailOrPhone, otp, isForgetPassword)) {
			return "Invalid OTP";
		}

		RegistrationEntity user = emailOrPhone.contains("@") ? registrationRepository.findByEmail(emailOrPhone)
				: registrationRepository.findByMobileNumber(emailOrPhone);

		if (user != null) {
			user.setPassword(passwordEncoder.encode(newPassword)); // Ensure password is hashed before saving
			registrationRepository.save(user);
			return "Password reset successfully";
		}

		return "User not found";
	}

//    public boolean resetPassword(String emailOrPhone, String newPassword, String otp, boolean isForgetPassword) {
//        if (otpService.verifyOtp(emailOrPhone, otp, isForgetPassword)) {
//            RegistrationEntity user = registrationRepository.findByEmail(emailOrPhone);
//            if (user == null) {
//                user = registrationRepository.findByMobileNumber(emailOrPhone);
//            }
//            if (user != null) {
//                user.setPassword(newPassword); // Ideally, hash the password before saving
//                registrationRepository.save(user);
//                return true;
//            }
//        }
//        return false;
//    }

}