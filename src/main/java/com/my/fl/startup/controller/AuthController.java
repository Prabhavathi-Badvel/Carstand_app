package com.my.fl.startup.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.my.fl.startup.constants.ZOROConstants;
import com.my.fl.startup.entity.RegistrationEntity;
import com.my.fl.startup.entity.UserOTPEntity;
import com.my.fl.startup.jwt.JwtProvider;
import com.my.fl.startup.jwt.JwtResponse;
import com.my.fl.startup.jwt.LoggedInResponse;
import com.my.fl.startup.jwt.LoginForm;
import com.my.fl.startup.jwt.SignUpForm;
import com.my.fl.startup.model.OTPRequest;
import com.my.fl.startup.model.ResetPasswordRequest;
import com.my.fl.startup.model.ResponseMessageDto;
import com.my.fl.startup.model.ResponseModel;
import com.my.fl.startup.model.VerifyOTPRequest;
import com.my.fl.startup.repo.RegistrationRepository;
import com.my.fl.startup.repo.UserOTPRepository;
import com.my.fl.startup.service.AdminLoginService;
import com.my.fl.startup.service.OTPService;
import com.my.fl.startup.service.UserPrinciple;
import com.my.fl.startup.service.UserService;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth/")
public class AuthController {

	@Autowired
	private AdminLoginService adminLoginService;

	@Autowired
	private RegistrationRepository registrationRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtProvider jwtProvider;

	@Autowired
	private OTPService otpService;

	@Autowired
	private UserOTPRepository otpRepository;

	@PostMapping("/signup")
	public ResponseEntity<ResponseMessageDto> registerUser(@Valid @RequestBody SignUpForm signUpRequest) {
		return userService.addUser(signUpRequest);
	}

	@PostMapping("/signin")
	public ResponseEntity<ResponseMessageDto> authenticateUser(@Valid @RequestBody LoginForm loginRequest,
			HttpServletRequest request) throws IOException {
		ResponseMessageDto response = new ResponseMessageDto();

		if (loginRequest.getPassword() == null) {
			response.setMessage("Error: Password is null");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		RegistrationEntity registrationEntity = registrationRepository
				.findByEmailORMobileNumberAndUserTypeNot(loginRequest.getUsername(), "A");
		if (registrationEntity == null) {
			response.setMessage("Error: User not found");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		if (!registrationEntity.getStatus().equals("ACTIVE")) {
			response.setMessage("Account status : " + registrationEntity.getStatus());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		} 
		
		Optional<UserOTPEntity> otpVerify = otpRepository
				.findByUserPhoneNumberAndUserEmail(registrationEntity.getMobileNumber(), registrationEntity.getEmail());

		if (loginRequest.getUsername().contains("@") && !otpVerify.get().isEmailVerify()) {
			response.setMessage("Your email is not yet verified, please verify it first");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

		} else if (!loginRequest.getUsername().contains("@") && !otpVerify.get().isSmsVerify()) {
			response.setMessage("Your mobile number is not yet verified, please verify it first");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		boolean isPasswordMatch = passwordEncoder.matches(loginRequest.getPassword(), registrationEntity.getPassword());
		if (!isPasswordMatch) {
			response.setMessage("Error: Incorrect password");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		String jwt = jwtProvider.generateJwtToken(loginRequest.getUsername(), request.getRemoteAddr());
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		UserPrinciple principle = (UserPrinciple) authentication.getPrincipal();
		LoggedInResponse jwtResponse = new LoggedInResponse(jwt, principle.getUsername(),
				principle.getCandidateID().toString(), loginRequest.getUsername(), registrationEntity.getFirstName(),
				principle.getEmail(),registrationEntity.getUserType() , Boolean.TRUE,"Success" );
		if(otpVerify.get().isSmsVerify()) {
			jwtResponse.setVerifiedMobile(otpVerify.get().getUserPhoneNumber());
		}else {
			jwtResponse.setVerifiedMobile("");
		}
		
		response.setMessage("User authenticated successfully");
		response.setStatus(true);
		response.setData(jwtResponse);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/admin/login")
	public ResponseEntity<ResponseMessageDto> authenticateAdminUser(@Valid @RequestBody LoginForm loginRequest,
			HttpServletRequest request) {
		return adminLoginService.authenticateAdmin(loginRequest, request);
	}

//	@PostMapping("/signin")
//	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginForm loginRequest, HttpServletRequest request)
//			throws IOException {
//		if (loginRequest.getPassword() == null) {
//			return ResponseEntity.badRequest().body("Error: Password is null");
//		}
//		RegistrationEntity registrationEntity = registrationRepository
//				.findByEmailAndUserTypeNot(loginRequest.getUsername(), "A");
//		if (registrationEntity == null) {
//			return ResponseEntity.badRequest().body("Error: User not found");
//		}
//		boolean isPasswordMatch = passwordEncoder.matches(loginRequest.getPassword(), registrationEntity.getPassword());
//		if (!isPasswordMatch) {
//			return ResponseEntity.badRequest().body("Error: Incorrect password");
//		}
//
//		String jwt = jwtProvider.generateJwtToken(loginRequest.getUsername(), request.getRemoteAddr());
//		Authentication authentication = authenticationManager.authenticate(
//				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
//		SecurityContextHolder.getContext().setAuthentication(authentication);
//
//		UserPrinciple principle = (UserPrinciple) authentication.getPrincipal();
//		return ResponseEntity.ok(new JwtResponse(jwt, principle.getUsername(), principle.getId().toString(), null,
//				principle.getEmail(), null, null, null, null, null));
//	}

	@PostMapping("/admin/signin")
	public ResponseEntity<?> authenticateAdmin(@Valid @RequestBody LoginForm loginRequest, HttpServletRequest request)
			throws IOException {
		System.out.println(passwordEncoder.encode("admin"));
		if (loginRequest.getPassword() == null) {
			return ResponseEntity.badRequest().body("Error: Password is null");
		}
		RegistrationEntity registrationEntity = registrationRepository
				.findByEmailAndUserType(loginRequest.getUsername(), "A");
		if (registrationEntity == null) {
			return ResponseEntity.badRequest().body("Error: User not found");
		}
		boolean isPasswordMatch = passwordEncoder.matches(loginRequest.getPassword(), registrationEntity.getPassword());
		if (!isPasswordMatch) {
			return ResponseEntity.badRequest().body("Error: Incorrect password");
		}

		String jwt = jwtProvider.generateJwtToken(loginRequest.getUsername(), request.getRemoteAddr());
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		UserPrinciple principle = (UserPrinciple) authentication.getPrincipal();
		return ResponseEntity.ok(new JwtResponse(jwt, principle.getUsername(), principle.getId().toString(), null,
				principle.getEmail(), null, null, null, null, null));
	}

	@PostMapping("/sendEmailOtp")
	public ResponseEntity<ResponseMessageDto> sendEmailOTP(@RequestBody OTPRequest request) throws Exception {
		// HttpStatus httpStatus;
		// String message = null;
		ResponseMessageDto response = new ResponseMessageDto();
		try {
			boolean isOtpSend = userService.sendEmailOtp(request);
			// httpStatus = HttpStatus.OK;
			if (isOtpSend) {
				response.setMessage("OTP Send To Email Successfully");
				response.setStatus(true);
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				response.setMessage("OTP Send Failed.Please Check Email");
				response.setStatus(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			response.setMessage("Error occurred while sending OTP..");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/sendPhoneOtp")
	public ResponseEntity<ResponseMessageDto> sendPhoneOTP(@RequestBody OTPRequest request) throws Exception {
		ResponseMessageDto message = new ResponseMessageDto();
		// HttpStatus httpStatus;
		try {
			boolean isOtpSend = userService.sendOtpToSMS(request);
			// httpStatus = HttpStatus.OK;
			if (isOtpSend) {
				message.setMessage("OTP Send to mobile number");
				message.setStatus(true);
				return new ResponseEntity<>(message, HttpStatus.OK);
			} else {
				message.setMessage("OTP Send Failed.Please Check mobile number");
				message.setStatus(false);
			}
		} catch (Exception e) {
			// httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

		}
		return new ResponseEntity<>(message, HttpStatus.OK);
	}

	@PostMapping("/verifyEmailOTP")
	public ResponseEntity<ResponseMessageDto> verifyEmailOTP(@RequestBody VerifyOTPRequest emailOtp) throws Exception {

		ResponseMessageDto message = new ResponseMessageDto();
		try {
			emailOtp.setIsForgetPassword(false);
			boolean isEmailVerified = userService.verifyEmailOtp(emailOtp);

			if (isEmailVerified) {
				message.setMessage("Email Verification Success");
				message.setStatus(true);
				return new ResponseEntity<>(message, HttpStatus.OK);

			} else if (ZOROConstants.IS_EMAIL_ALREADY_VERIFIED) {
				message.setMessage("Email already verified");
				message.setStatus(true);
				return new ResponseEntity<>(message, HttpStatus.OK);
			} else {
				message.setMessage("invalid otp");
				message.setStatus(false);
				return new ResponseEntity<>(message, HttpStatus.OK);
			}
		} catch (UsernameNotFoundException e) {
			message.setMessage(e.getMessage());
			message.setStatus(false);
			return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			message.setMessage("Internal Server Error");
			message.setStatus(false);
			return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/verifySmsOTP")
	public ResponseEntity<ResponseMessageDto> verifySMSOTP(@RequestBody VerifyOTPRequest smsOtp) {

		ResponseMessageDto message = new ResponseMessageDto();
//        HttpStatus httpStatus;
//        String message = null;
		try {
			smsOtp.setIsForgetPassword(false);
			boolean isEmailVerified = userService.verifySmsOTP(smsOtp);
			// httpStatus = HttpStatus.OK;
			if (isEmailVerified) {
				message.setMessage("SMS Verification Success");
				message.setStatus(true);
				return new ResponseEntity<>(message, HttpStatus.OK);

			} else if (ZOROConstants.IS_EMAIL_ALREADY_VERIFIED) {
				message.setMessage("User Already Verified");
				message.setStatus(true);
				return new ResponseEntity<>(message, HttpStatus.OK);

			} else {
				message.setMessage("invalid otp");
				message.setStatus(false);
				return new ResponseEntity<>(message, HttpStatus.OK);

			}
		} catch (UsernameNotFoundException e) {
			message.setMessage(e.getMessage());
			message.setStatus(false);
			return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			message.setMessage("Internal Server Error");
			message.setStatus(false);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/forgotPassword")
	public ResponseEntity<ResponseMessageDto> forgotPassword(@RequestBody OTPRequest request) {
		ResponseMessageDto message = new ResponseMessageDto();
		// request.setForgetPassword(true); // Ensure that the request indicates a
		// forgot password scenario
		boolean isOtpSent = otpService
				.sendForgotPasswordOtp(request.getEmail() != null ? request.getEmail() : request.getPhoneNumber());
		if (isOtpSent) {
			message.setMessage("OTP sent for reset password .");
			message.setStatus(true);
		} else {
			message.setMessage("Failed to send OTP. Please check the email.");
			message.setStatus(false);
		}
		return new ResponseEntity<>(message, HttpStatus.OK);

	}

//    @PostMapping("/verifyForgotPasswordOtp")
//    public ResponseEntity<ResponseMessageDto> verifyForgotPasswordOtp(@RequestBody VerifyOTPRequest request) {
//        ResponseMessageDto message=new ResponseMessageDto();
//        try {
//            boolean isOtpValid = otpService.verifyForgotPasswordOtp(request.getEmail(), request.getOtp());
//            if (isOtpValid) {
//                message.setMessage("OTP verified successfully.");
//                message.setStatus(true);
//                return new ResponseEntity<>(message, HttpStatus.OK);
//            } else {
//                message.setMessage("Invalid OTP.");
//                message.setStatus(false);
//                return new ResponseEntity<>(message, HttpStatus.OK);
//            }
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

	@PostMapping("/resetPassword")
	public ResponseEntity<ResponseModel> resetPassword(@RequestBody ResetPasswordRequest request) {
		ResponseModel response = new ResponseModel();
		String result = userService.resetPassword(request.getEmailOrPhone(), request.getNewPassword(), request.getOtp(),
				true);

		switch (result) {
		case "Password reset successfully":
			response.setError("false");
			response.setMsg(result);
			return ResponseEntity.ok(response);

		case "Invalid OTP":
			response.setError("true");
			response.setMsg(result);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

		case "User not found":
			response.setError("true");
			response.setMsg(result);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

		default:
			response.setError("true");
			response.setMsg("An unexpected error occurred");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

//    @PostMapping("/resetPassword")
//    public ResponseEntity<ResponseMessageDto> resetPassword(@RequestBody ResetPasswordRequest request) {
//        ResponseMessageDto message=new ResponseMessageDto();
//        boolean isPasswordReset = userService.resetPassword(
//                request.getEmailOrPhone(),
//                request.getNewPassword(),
//                request.getOtp(),
//                true
//        );
//        if (isPasswordReset) {
//            message.setMessage("Password reset successfully.");
//            message.setStatus(true);
//            return new ResponseEntity<>(message, HttpStatus.OK);
//        } else {
//            message.setMessage("Invalid OTP.");
//            message.setStatus(false);
//            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
//        }
//    }

	@PostMapping("logout")
	public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
		HttpStatus httpStatus = HttpStatus.OK;
		jwtProvider.deleteToken(token);
		return ResponseEntity.status(httpStatus).body("Successfully Logout");
	}

}