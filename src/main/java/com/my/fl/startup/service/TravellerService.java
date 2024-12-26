package com.my.fl.startup.service;

import com.my.fl.startup.constants.ZOROConstants;
import com.my.fl.startup.entity.RegistrationEntity;
import com.my.fl.startup.entity.TravellerOTPEntity;
import com.my.fl.startup.entity.TravellerRegistrationEntity;
import com.my.fl.startup.entity.UserOTPEntity;
import com.my.fl.startup.jwt.JwtProvider;
import com.my.fl.startup.jwt.JwtResponse;
import com.my.fl.startup.jwt.LoginForm;
import com.my.fl.startup.model.*;
import com.my.fl.startup.model.traveller.TravellerRegistrationResponseDTO;
import com.my.fl.startup.repo.RegistrationRepository;
import com.my.fl.startup.repo.TravellerRepository;
import com.my.fl.startup.repo.UserOTPRepository;
import com.my.fl.startup.repo.traveller.TravellerOTPRepo;
import com.my.fl.startup.template.EmailTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class TravellerService {

	@Autowired
	private TravellerRepository travellerRepository;

	@Autowired
	private RegistrationRepository registrationRepository;

	@Autowired
	private UserOTPRepository userOTPRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private OTPService otpService;

	@Autowired
	private JwtProvider jwtProvider;

	@Autowired
	private AuthenticationManager authenticationManager;

	private final EmailService emailService;

	private final SmsHandler smsHandler;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

	private static final Pattern MOBILE_NUMBER_PATTERN = Pattern.compile("^\\+?[0-9]{10,15}$");
	@Autowired
	private TravellerOTPRepo travellerOTPRepo;

	public TravellerService(EmailService emailService, SmsHandler smsHandler) {
		this.emailService = emailService;
		this.smsHandler = smsHandler;
	}

	public JwtResponse authenticateTraveller(LoginForm loginRequest, HttpServletRequest request) throws IOException {
		TravellerRegistrationEntity traveller = travellerRepository.findByTravellerEmail(loginRequest.getUsername());

		if (traveller == null) {
			throw new UsernameNotFoundException("Error: Traveller not found");
		}

		boolean isPasswordMatch = passwordEncoder.matches(loginRequest.getPassword(), traveller.getTravelledPassword());
		if (!isPasswordMatch) {
			throw new BadCredentialsException("Error: Incorrect password");
		}

		String jwt = jwtProvider.generateJwtToken(loginRequest.getUsername(), request.getRemoteAddr());
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		UserPrinciple principle = (UserPrinciple) authentication.getPrincipal();
		
		return new JwtResponse(jwt,
				principle.getUsername(), 
				principle.getId().toString(), 
				null,
				traveller.getTravellerName(),
				principle.getEmail(),
				null,
				null, 
				"Success",
				null);
	}

	public ResponseModel addTraveller(TravellerRequest request) {
		ResponseModel responseModel = new ResponseModel();

		try {
			TravellerRegistrationEntity entity = mapTravellerEntity(request);
			TravellerRegistrationEntity travellerRegistration = travellerRepository
					.findByTravellerEmailOrTravellerMobile(request.getTravellerEmail(),
							request.getTravellerMobileNumber());

			if (travellerRegistration != null) {
				throw new IllegalArgumentException("Traveller already exists with this name or email");
			}

			boolean isEmailOTPSent = sendOTPToTravellerEmail(entity);
			boolean isMobileOTPSent = sendOTPToTravellerMobile(entity);

			if (isEmailOTPSent && isMobileOTPSent) {
				entity.setRegistrationDate(LocalDate.now()); // If using LocalDateTime
				travellerRepository.save(entity);
				responseModel.setError("false");
				responseModel.setMsg("Traveller added successfully");
				responseModel.setData(entity);
			}
		} catch (Exception e) {
			responseModel.setError("true");
			responseModel.setMsg("Error adding Traveller Details: " + e.getMessage());
		}

		return responseModel;
	}

	private boolean sendOTPToTravellerMobile(TravellerRegistrationEntity registrationEntity) {

		String otp = OTPService.generateOTP(true);
		registrationEntity.setTravellerMobileOtp(otp);
		return smsHandler.sendSMSMessage(registrationEntity.getTravellerMobile(), otp);

	}

	private boolean sendOTPToTravellerEmail(TravellerRegistrationEntity registrationEntity) {
		String otp = OTPService.generateOTP(true);
		boolean messageSent;
		EmailTemplate template = new EmailTemplate("static/send-otp.html");
		Map<String, String> replacements = new HashMap<>();

		replacements.put("user", registrationEntity.getTravellerEmail());
		replacements.put("otp", otp);
		String message = template.getTemplate(replacements);
		messageSent = emailService.sendEmailMessage(registrationEntity.getTravellerEmail(), message,
				"OTP For Traveller Registration at Zoro");
		registrationEntity.setTravellerEmailOtp(otp);
		return messageSent;
	}

	private boolean isValidTraveller(TravellerRequest request) {
		RegistrationEntity user = registrationRepository.findByEmailOrMobileNumber(request.getTravellerEmail(),
				request.getTravellerMobileNumber());
		return (user != null);
	}

	public ResponseModel updateTravellerById(String id, TravellerRequest request) {

		return new ResponseModel("Updated successfully", "false");
	}

	public ResponseModel updateTravellerByUsername(TravellerRequest request) {

		TravellerRegistrationEntity existingTraveller = travellerRepository
				.findByTravellerEmailOrTravellerMobile(request.getTravellerEmail(), request.getTravellerMobileNumber());

		if (existingTraveller == null) {
			return new ResponseModel("Traveller not found", "true");
		}
		Optional<UserOTPEntity> userOtpEntityOptional = userOTPRepository.findById(existingTraveller.getTravellerId());
		if (userOtpEntityOptional.isPresent()) {
			UserOTPEntity userOtpEntity = userOtpEntityOptional.get();
			existingTraveller.setTravellerEmail(userOtpEntity.getUserEmail());
			existingTraveller.setTravellerEmailOtp(userOtpEntity.getEmailOtp());
			existingTraveller.setTravellerMobile(userOtpEntity.getUserPhoneNumber());
			existingTraveller.setTravellerMobileOtp(userOtpEntity.getPhoneOtp());
		}
		return new ResponseModel("false", "Updated successfully");
	}

	public List<TravellerRegistrationEntity> getAllTravellers() {
		return travellerRepository.findAll();
	}

	public Optional<TravellerRegistrationEntity> getTravellerById(Long id) {
		return travellerRepository.findById(id);
	}

	public TravellerRegistrationEntity getTravellerByUsername(String username) throws Exception {

		if (EMAIL_PATTERN.matcher(username).matches()) {
			return travellerRepository.findByTravellerEmail(username);
		} else if (MOBILE_NUMBER_PATTERN.matcher(username).matches()) {
			return travellerRepository.findByTravellerMobile(username);
		} else {
			throw new Exception("Invalid username");
		}
	}

	private TravellerRegistrationEntity mapTravellerEntity(TravellerRequest request) {
		TravellerRegistrationEntity entity = new TravellerRegistrationEntity();
		// entity.setTravellerId(ZOROConstants.ZOT + Instant.now().toString());
		entity.setTravellerName(request.getTravellerName());
		entity.setTravellerMobile(request.getTravellerMobileNumber());
		entity.setTravellerEmail(request.getTravellerEmail());
		entity.setStatus(Status.INACTIVE.name());
		entity.setUserType("T");
		entity.setTravelledPassword(passwordEncoder.encode(request.getTravelledPassword()));
//        entity.setRegistrationDate(LocalDate.now());

		return entity;
	}

	public boolean verifyTravellerByEmail(VerifyOTPRequest request) throws Exception {

		String email = request.getEmail();
		TravellerRegistrationEntity user = travellerRepository.findByTravellerEmail(email);

		if (user != null) {
			String emailVerifiedStatus = user.getTravellerEmailVerified();

			// Check if emailVerifiedStatus is not null and equals to "verified"
			if ("verified".equalsIgnoreCase(emailVerifiedStatus)) {
				// Return false as the user is already verified
				return false;
			} else if (request.getOtp().equalsIgnoreCase(user.getTravellerEmailOtp())) {
				// Update the status and verification if OTP matches
				travellerRepository.updateStatusAndVerificationByEmail(Status.ACTIVE.name(), "verified", email);
				return true;
			} else {
				// Throw an exception if the OTP is invalid
				throw new Exception("Invalid OTP");
			}
		} else {
			// Handle the case where user is not found
			throw new Exception("User not found");
		}
	}

	public boolean verifyTravellerByMobileNumber(VerifyOTPRequest request) throws Exception {

		String phoneNumber = request.getPhoneNumber();
		TravellerRegistrationEntity user = travellerRepository.findByTravellerMobile(phoneNumber);
		if (user != null) {
			String mobileVerifiedStatus = user.getTravellerEmailVerified();

			// Check if emailVerifiedStatus is not null and equals to "verified"
			if ("verified".equalsIgnoreCase(mobileVerifiedStatus)) {
				// Return false as the user is already verified
				return false;
			} else if (request.getOtp().equalsIgnoreCase(user.getTravellerMobileOtp())) {
				// Update the status and verification if OTP matches
				travellerRepository.updateStatusAndVerificationByMobileNumber(Status.ACTIVE.name(), "verified",
						phoneNumber);
				return true;
			} else {
				// Throw an exception if the OTP is invalid
				throw new Exception("Invalid OTP");
			}
		} else {
			// Handle the case where user is not found
			throw new Exception("User not found");
		}

	}

	public ResponseModel login(String username, String password, HttpServletRequest request) {
		ResponseModel response = new ResponseModel();
		TravellerRegistrationEntity user = null;

		if (EMAIL_PATTERN.matcher(username).matches()) {
			user = travellerRepository.findByTravellerEmail(username);
		} else if (MOBILE_NUMBER_PATTERN.matcher(username).matches()) {
			user = travellerRepository.findByTravellerMobile(username);
		}

		if (user == null || !passwordEncoder.matches(password, user.getTravelledPassword())) {
			response.setError("true");
			response.setMsg("Invalid identifier or password");
		} else {
			TravellerRegistrationResponseDTO registrationResponseDTO = new TravellerRegistrationResponseDTO();
			BeanUtils.copyProperties(user, registrationResponseDTO);
			String jwt = jwtProvider.generateJwtToken(username, request.getRemoteAddr());
			response.setError("false");
			response.setMsg("Login successful");
			registrationResponseDTO.setToken("Bearer " + jwt);
			response.setData(registrationResponseDTO);
		}

		return response;
	}

	public String resetForgotTravellerPassword(ForgotPasswordModel request) {

		if (Boolean.TRUE.equals(request.getForgotPassword())) {
			TravellerRegistrationEntity travellerRegistration = request.getEmailOrPhone().contains("@")
					? travellerRepository.findByTravellerEmail(request.getEmailOrPhone())
					: travellerRepository.findByTravellerMobile(request.getEmailOrPhone());

			if (travellerRegistration != null) {
				travellerRegistration.setTravelledPassword(passwordEncoder.encode(request.getNewPassword())); // Ensure
																												// password
																												// is
																												// hashed
																												// before
																												// saving
				travellerRepository.save(travellerRegistration);
				return "Forgot password changed successfully";
			}
		}
		if (Boolean.TRUE.equals(request.getResetPpassword())) {
			TravellerRegistrationEntity travellerRegistration = request.getEmailOrPhone().contains("@")
					? travellerRepository.findByTravellerEmail(request.getEmailOrPhone())
					: travellerRepository.findByTravellerMobile(request.getEmailOrPhone());

			if (travellerRegistration != null) {
				travellerRegistration.setTravelledPassword(passwordEncoder.encode(request.getNewPassword())); // Ensure
																												// password
																												// is
																												// hashed
																												// before
																												// saving
				travellerRepository.save(travellerRegistration);
				return "Password reset successfully";
			} else {
				return "Old password is Invalid";
			}
		}
		return "User not found";
	}

	public boolean verifyTravellerForgotPasswordOtp(VerifyOTPRequest request) throws Exception {
		if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
			Optional<TravellerOTPEntity> travellerOTPEntityOptional = travellerOTPRepo
					.findFirstByUserPhoneNumberAndForgotPasswordVerifiedOrderByLastUpdatedDesc(request.getPhoneNumber(),
							false);
			if (!travellerOTPEntityOptional.isEmpty()) {
				TravellerOTPEntity travellerOTP = travellerOTPEntityOptional.get();
				if (travellerOTP.getForgetPasswordOtp().equals(request.getOtp())) {
					travellerOTP.setForgotPasswordVerified(true);
					travellerOTPRepo.save(travellerOTP);
					return true;
				}
			}
		} else if (request.getEmail() != null && !request.getEmail().isEmpty()) {
			Optional<TravellerOTPEntity> travellerOTPEntityOptional = travellerOTPRepo
					.findFirstByUserEmailAndForgotPasswordVerifiedOrderByLastUpdatedDesc(request.getEmail(), false);
			if (!travellerOTPEntityOptional.isEmpty()) {
				TravellerOTPEntity travellerOTP = travellerOTPEntityOptional.get();
				if (travellerOTP.getForgetPasswordOtp().equals(request.getOtp())) {
					travellerOTP.setForgotPasswordVerified(true);
					travellerOTPRepo.save(travellerOTP);
					return true;
				}
			}
		}
		throw new Exception("Invalid OTP");
	}

	public String resetTravellerPassword(ForgotPasswordModel request) {

		TravellerRegistrationEntity travellerRegistration = request.getEmailOrPhone().contains("@")
				? travellerRepository.findByTravellerEmail(request.getEmailOrPhone())
				: travellerRepository.findByTravellerMobile(request.getEmailOrPhone());

		if (travellerRegistration != null) {
			boolean isPasswordMatch = passwordEncoder.matches(request.getOldPassword(),
					travellerRegistration.getTravelledPassword());

			if (isPasswordMatch) {
				travellerRegistration.setTravelledPassword(passwordEncoder.encode(request.getNewPassword())); // Ensure
																												// password
																												// is
																												// hashed
																												// before
																												// saving
				travellerRepository.save(travellerRegistration);
				return "Password reset successfully";
			} else {
				return "Old password is Invalid";
			}
		}
		return "User not found";
	}
}