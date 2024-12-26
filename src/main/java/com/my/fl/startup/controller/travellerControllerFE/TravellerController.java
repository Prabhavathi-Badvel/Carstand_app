package com.my.fl.startup.controller.travellerControllerFE;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.my.fl.startup.entity.TravellerRegistrationEntity;
import com.my.fl.startup.jwt.JwtAuthTokenFilter;
import com.my.fl.startup.jwt.JwtProvider;
import com.my.fl.startup.jwt.JwtResponse;
import com.my.fl.startup.jwt.LoginForm;
import com.my.fl.startup.jwt.TokenInfo;
import com.my.fl.startup.model.ForgotPasswordModel;
import com.my.fl.startup.model.OTPRequest;
import com.my.fl.startup.model.ResponseMessageDto;
import com.my.fl.startup.model.ResponseModel;
import com.my.fl.startup.model.TravellerRequest;
import com.my.fl.startup.model.VerifyOTPRequest;
import com.my.fl.startup.repo.TokenRepository;
import com.my.fl.startup.service.OTPService;
import com.my.fl.startup.service.TravellerService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/traveller")
public class TravellerController {

	@Autowired
	private TravellerService travellerService;
	@Autowired
	private OTPService otpService;
	@Autowired
	private JwtProvider jwtProvider;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtAuthTokenFilter jwtAuthTokenFilter;
	@Autowired
	private TokenRepository tokenRepository;


	@PostMapping("/addTraveller")
	public ResponseEntity<?> saveTraveller(@RequestBody TravellerRequest request) {
		ResponseModel response = travellerService.addTraveller(request);
		return response.getError().equalsIgnoreCase("false") ?
				new ResponseEntity<>(response.getMsg(), HttpStatus.OK) :
				new ResponseEntity<>(response.getMsg(), HttpStatus.BAD_REQUEST);
	}



	@PostMapping("/verifyTravellerEmail")
	public ResponseEntity<?> verifyTravellerEmail(@RequestBody VerifyOTPRequest request) throws Exception {
		boolean isOtpSend = travellerService.verifyTravellerByEmail(request);
		ResponseModel response = new ResponseModel();
		String message = null;
		if (isOtpSend) {
			message ="Email Verified Successfully";
			response.setError("False");
			response.setMsg(message);
		}
		else if (!isOtpSend) {
			message ="Email is Already Verified Successfully";
			response.setError("False");
			response.setMsg(message);
		}else{
			message ="EMAIL OTP verification Failed. Please try again.";
			response.setError("True");
			response.setMsg(message);
		}

		return response.getError().equalsIgnoreCase("false") ?
				new ResponseEntity<>(response.getMsg(), HttpStatus.OK) :
				new ResponseEntity<>(response.getMsg(), HttpStatus.BAD_REQUEST);
	}


	@PostMapping("/verifyTravellerMobileNumber")
	public ResponseEntity<?> verifyTravellerMobileNumber(@RequestBody VerifyOTPRequest request) throws Exception {

		boolean isOtpSend = travellerService.verifyTravellerByMobileNumber(request);

		ResponseModel response = new ResponseModel();
		String message = null;
		if (isOtpSend) {
			message ="Mobile Number Verified Successfully";
			response.setError("False");
			response.setMsg(message);
		}else if (!isOtpSend) {
			message ="Mobile Number is Already Verified Successfully";
			response.setError("False");
			response.setMsg(message);
		}
		else{
			message ="SMS OTP verification Failed. Please try again.";
			response.setError("True");
			response.setMsg(message);
		}

		return response.getError().equalsIgnoreCase("false") ?
				new ResponseEntity<>(response.getMsg(), HttpStatus.OK) :
				new ResponseEntity<>(response.getMsg(), HttpStatus.BAD_REQUEST);
	}

	@PutMapping("/updateTravellerById/{id}")
	public ResponseEntity<?> updateTravellerById(@PathVariable String id, @RequestBody TravellerRequest request) {
		ResponseModel response = travellerService.updateTravellerById(id, request);
		return response.getError().equals("false") ?
				new ResponseEntity<>(response.getMsg(), HttpStatus.OK) :
				new ResponseEntity<>(response.getMsg(), HttpStatus.BAD_REQUEST);
	}

	@PutMapping("/updateTraveller")
	public ResponseEntity<?> updateTravellerByUsername(@RequestBody TravellerRequest request) {
		ResponseModel response = travellerService.updateTravellerByUsername(request);
		return response.getError().equals("false") ?
				new ResponseEntity<>(response.getMsg(), HttpStatus.OK) :
				new ResponseEntity<>(response.getMsg(), HttpStatus.BAD_REQUEST);
	}


	@GetMapping("/getAllTravellers")
	public List<TravellerRegistrationEntity> getAllTravellers() {
		return travellerService.getAllTravellers();
	}


	@GetMapping("/getTraveller/{username}")
	public TravellerRegistrationEntity getTravellerByUsername(@PathVariable String username) throws Exception {
		TravellerRegistrationEntity entity = travellerService.getTravellerByUsername(username);
		return entity;
	}

	@PostMapping("/login")
	public ResponseEntity<ResponseModel> login(@RequestBody LoginForm loginRequest, HttpServletRequest request) {
		ResponseModel response = travellerService.login(loginRequest.getUsername(), loginRequest.getPassword(),request);
		return new ResponseEntity<>(response, response.getError().equals("false") ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
	}

	@PostMapping("/logout")
	public ResponseEntity<Object> logout(HttpServletRequest request) {
		try {
			// Extract JWT token from the request
			String jwt = jwtAuthTokenFilter.getJwt(request);

			// Return an error if no token is found
			if (jwt == null || jwt.isEmpty()) {
				return new ResponseEntity<>("No JWT Token provided", HttpStatus.BAD_REQUEST);
			}

			// Validate the JWT token
			if (jwtProvider.validateJwtToken(jwt)) {
				// If valid, get token info
				TokenInfo tokenInfo = jwtProvider.getTokenInfo(jwt);

				if (tokenInfo != null) {
					// Delete the token and get the result
					Boolean isDeleted = jwtProvider.deleteTokenForTraveller(tokenInfo.getTokenId(),tokenInfo.getUsername());

					if (isDeleted) {
						return new ResponseEntity<>("Traveller logged out successfully", HttpStatus.OK);
					} else {
						return new ResponseEntity<>("Token not found or already expired", HttpStatus.NOT_FOUND);
					}
				} else {
					return new ResponseEntity<>("Unable to extract token information", HttpStatus.BAD_REQUEST);
				}
			} else {
				// Return an unauthorized response if JWT is invalid
				return new ResponseEntity<>("Invalid JWT Token", HttpStatus.UNAUTHORIZED);
			}
		} catch (ExpiredJwtException e) {
			// Handle specific case where JWT has expired
			return new ResponseEntity<>("Expired JWT Token: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
		} catch (MalformedJwtException e) {
			// Handle specific JWT exception
			return new ResponseEntity<>("Malformed JWT Token: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			// Handle general exceptions
			return new ResponseEntity<>("Error processing the request: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@PostMapping("/signin")
	public ResponseEntity<ResponseMessageDto> authenticateTraveller(@Valid @RequestBody LoginForm loginRequest, HttpServletRequest request) {
		ResponseMessageDto response = new ResponseMessageDto();

		try {
			JwtResponse jwtResponse = travellerService.authenticateTraveller(loginRequest, request);
			response.setMessage("Traveller authenticated successfully");
			response.setStatus(true);
			response.setData(jwtResponse);
			return ResponseEntity.ok(response);
		} catch (UsernameNotFoundException | BadCredentialsException e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			response.setMessage("Internal Server Error");
			response.setStatus(false);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@PostMapping("/forgotTravellerPassword")
	public ResponseEntity<ResponseMessageDto> forgotPassword(@RequestBody OTPRequest request) {
		ResponseMessageDto message=new ResponseMessageDto();
		boolean isOtpSent = otpService.sendForgotPasswordOtpForTraveller(
				request.getEmail() != null ? request.getEmail() : request.getPhoneNumber()
		);
		if (isOtpSent) {
			message.setMessage("OTP sent for reset password .");
			message.setStatus(true);
			if (request.getEmail() != null){
				message.setData("OTP Generated Using Email : "+ request.getEmail());
			}else {
				message.setData("OTP Generated Using PhoneNo : "+ request.getPhoneNumber());
			}
		} else {
			message.setMessage("Failed to send OTP. Please check the email.");
			message.setStatus(false);
		}
		return new ResponseEntity<>(message, HttpStatus.OK);

	}


	@PostMapping("/verifyTravellerForgotPasswordOtp")
	public ResponseEntity<Object> verifyForgotPasswordOtp(@RequestBody VerifyOTPRequest request) throws Exception {
		ResponseMessageDto message=new ResponseMessageDto();
		boolean isVerified = travellerService.verifyTravellerForgotPasswordOtp(request);
		if (isVerified) {
			message.setMessage("OTP verified.");
			message.setStatus(true);
		} else {
			message.setMessage("Invalid OTP");
			message.setStatus(false);
		}
		return new ResponseEntity<>(message, HttpStatus.OK);
	}

	@PostMapping("/resetForgotTravellerPassword")
	public ResponseEntity<ResponseModel> resetForgotTravellerPassword(@RequestBody ForgotPasswordModel request) {
		ResponseModel response = new ResponseModel();
		String result = travellerService.resetForgotTravellerPassword(request);

		switch (result) {
			case "Forgot password changed successfully", "Password changed successfully" -> {
				response.setError("false");
				response.setMsg(result);
				return ResponseEntity.ok(response);
			}
			case "Old password is Invalid", "User not found" -> {
				response.setError("true");
				response.setMsg(result);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}
			default -> {
				response.setError("true");
				response.setMsg("An unexpected error occurred");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
			}
		}
	}

	@PostMapping("/resetTravellerPassword")
	public ResponseEntity<ResponseModel> resetTravellerPassword(@RequestBody ForgotPasswordModel request) {
		ResponseModel response = new ResponseModel();
		String result = travellerService.resetTravellerPassword(request);

		switch (result) {
			case "Forgot password changed successfully", "Password changed successfully" -> {
				response.setError("false");
				response.setMsg(result);
				return ResponseEntity.ok(response);
			}
			case "Old password is Invalid", "User not found" -> {
				response.setError("true");
				response.setMsg(result);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}
			default -> {
				response.setError("true");
				response.setMsg("An unexpected error occurred");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
			}
		}
	}

}