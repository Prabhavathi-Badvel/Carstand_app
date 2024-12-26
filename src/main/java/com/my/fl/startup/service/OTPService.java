package com.my.fl.startup.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.my.fl.startup.entity.RegistrationEntity;
import com.my.fl.startup.entity.TravellerOTPEntity;
import com.my.fl.startup.entity.TravellerRegistrationEntity;
import com.my.fl.startup.entity.UserOTPEntity;
import com.my.fl.startup.model.OTPRequest;
import com.my.fl.startup.repo.RegistrationRepository;
import com.my.fl.startup.repo.TokenRepository;
import com.my.fl.startup.repo.TravellerRepository;
import com.my.fl.startup.repo.UserOTPRepository;
import com.my.fl.startup.repo.UserRepository;
import com.my.fl.startup.repo.traveller.TravellerOTPRepo;
import com.my.fl.startup.template.EmailTemplate;

@Service
public class OTPService {

	private final EmailService emailService;

	private final SmsHandler smsHandler;

	@Autowired
	public OTPService(EmailService emailService, SmsHandler smsHandler, TokenRepository tokenRepository) {
		this.emailService = emailService;
		this.smsHandler = smsHandler;
		this.tokenRepository = tokenRepository;
	}

	@Autowired
	private UserOTPRepository userOtpRepository;
	@Autowired
	private UserRepository storeRepository;

	@Autowired
	private RegistrationRepository registrationRepository;
	@Autowired
	private TravellerRepository travellerRepository;
	@Autowired
	private TravellerOTPRepo travellerOTPRepo;
	private final TokenRepository tokenRepository;

	public boolean sendOtpToEmail(String email, Boolean isForgetPassword) {
		Optional<UserOTPEntity> userOtpOptional = userOtpRepository.findFirstByUserEmailOrderByCreatedOnAsc(email);
		String otp = OTPService.generateOTP(true);
		boolean messageSent;
		EmailTemplate template = new EmailTemplate("static/send-otp.html");
		Map<String, String> replacements = new HashMap<>();

		replacements.put("user", email);
		replacements.put("otp", otp);
		String message = template.getTemplate(replacements);
		messageSent = emailService.sendEmailMessage(email, message, "OTP Form Carstand");
		if (messageSent) {
			UserOTPEntity userOtp = new UserOTPEntity();
			if (userOtpOptional.isPresent()) {
				userOtp = userOtpOptional.get();
				if (isForgetPassword) {
					userOtp.setForgetPasswordOtp(otp);
				} else {
					userOtp.setEmailOtp(otp);
				}
				userOtp.setEmailOtpDate(new Date());
			}
			userOtpRepository.save(userOtp);
		}
		return messageSent;
	}

	public boolean sendOtpToPhoneNumber(String phoneNumber) {

		Optional<UserOTPEntity> userOtpOptional = userOtpRepository.findByUserPhoneNumber(phoneNumber);
		String otp = OTPService.generateOTP(true);
		boolean messageSent = smsHandler.sendSMSMessage(phoneNumber, otp);
		if (messageSent) {
			if (userOtpOptional.isPresent()) {
				UserOTPEntity userOtp = userOtpOptional.get();
				userOtp.setPhoneOtp(otp);
				userOtp.setPhoneOtpDate(new Date());
				userOtpRepository.save(userOtp);
			}
		}
		return messageSent;
	}

	public static String generateOTP(boolean isOTP) {
		Random random = new Random();
		int lowerBound = isOTP ? 100000 : 1000;
		return String.valueOf(lowerBound + random.nextInt(999999 - lowerBound + 1));
	}

	public boolean createUserOTP(String Id, String email, String mobileNumber) {

		UserOTPEntity userOtp = new UserOTPEntity();
		userOtp.setUserOtpId(Id);
		userOtp.setUserEmail(email);
		userOtp.setActive(0);
		userOtp.setCreatedOn(LocalDateTime.now().toString());
		userOtp.setUserPhoneNumber(mobileNumber);
		userOtpRepository.save(userOtp);
		OTPRequest otpRequest = createOTPRequest(email, mobileNumber);
		boolean isEmailSent = sendEmailOtp(otpRequest);
		boolean isPhoneOtpSent = sendOtpToSMS(otpRequest);

		return (isEmailSent || isPhoneOtpSent);
	}

	private OTPRequest createOTPRequest(String userEmail, String phoneNumber) {
		OTPRequest otpRequest = new OTPRequest();
		otpRequest.setEmail(userEmail);
		otpRequest.setPhoneNumber(phoneNumber);
		otpRequest.setForgetPassword(false);
		return otpRequest;
	}

	public boolean sendEmailOtp(OTPRequest request) {
		RegistrationEntity userOptional = registrationRepository.findByEmail(request.getEmail());
		if (null != userOptional) {
			String userEmail = request.getEmail();
			return (request.getForgetPassword() ? sendOtpToEmail(userEmail, true) : sendOtpToEmail(userEmail, false));
		}
		return false;
	}

	public boolean sendOtpToSMS(OTPRequest request) {
		RegistrationEntity user = registrationRepository.findByMobileNumber(request.getPhoneNumber());
		if (null != user) {
			String userPhoneNumber = request.getPhoneNumber();
			return sendOtpToPhoneNumber(userPhoneNumber);
		}
		return false;
	}

	public boolean sendOtpToSMSForTraveller(OTPRequest request) {
		TravellerRegistrationEntity user = travellerRepository.findByTravellerMobile(request.getPhoneNumber());
		if (null != user) {
			String userPhoneNumber = request.getPhoneNumber();
			return sendOtpToPhoneNumber(userPhoneNumber);
		}
		return false;
	}

//    public boolean sendForgotPasswordOtp(String emailOrPhone) {
//        RegistrationEntity userOptional = registrationRepository.findByEmail(emailOrPhone);
//        if (userOptional == null) {
//            userOptional = registrationRepository.findByMobileNumber(emailOrPhone);
//        }
//        if (userOptional != null) {
//            OTPRequest otpRequest = createOTPRequest(userOptional.getEmail(), userOptional.getMobileNumber());
//            return sendEmailOtp(otpRequest) && sendOtpToSMS(otpRequest);
//        }
//        return false;
//    }

	public boolean sendForgotPasswordOtp(String emailOrPhone) {
		RegistrationEntity user = registrationRepository.findByEmail(emailOrPhone);
		if (user == null) {
			user = registrationRepository.findByMobileNumber(emailOrPhone);
		}
		if (user != null) {
//            //String otp = generateOTP(true);
//            OTPRequest otpRequest = createOTPRequest(user.getEmail(), user.getMobileNumber());
//
//            UserOTPEntity userOtpEntity = new UserOTPEntity();
//            userOtpEntity.setForgetPasswordOtp(otp);
//            if (emailOrPhone.contains("@")) {
//                userOtpEntity.setUserEmail(user.getEmail());
//            } else {
//                userOtpEntity.setUserPhoneNumber(user.getMobileNumber());
//            }
//            userOtpRepository.save(userOtpEntity);

			boolean isOtpSent = emailOrPhone.contains("@") ? sendOtpToEmail(emailOrPhone, true)
					: sendOtpToSMS(new OTPRequest());
			return isOtpSent;
		}
		return false;
	}

	public boolean verifyOtp(String emailOrPhone, String otp, boolean isForgetPassword) {
		UserOTPEntity userOtp = emailOrPhone.contains("@")
				? userOtpRepository.findFirstByUserEmailOrderByCreatedOnAsc(emailOrPhone).orElse(null)
				: userOtpRepository.findFirstByUserPhoneNumberOrderByCreatedOnAsc(emailOrPhone).orElse(null);

		// if (userOtp != null) {
		// return isForgetPassword ? otp.equals(userOtp.getForgetPasswordOtp()) :
		// otp.equals(userOtp.getEmailOtp());
		// }
		if (emailOrPhone.contains("@")) {
			return otp.equals(userOtp.getEmailOtp()) || otp.equals(userOtp.getForgetPasswordOtp());
		} else {
			return otp.equals(userOtp.getPhoneOtp());
		}
	}

//    public boolean verifyOtp(String emailOrPhone, String otp, boolean isForgetPassword) {
//        UserOTPEntity userOtp = userOtpRepository.findFirstByUserEmailOrderByCreatedOnAsc(emailOrPhone)
//                .orElse(userOtpRepository.findByUserPhoneNumber(emailOrPhone).orElse(null));
//
//        if (userOtp != null) {
//            return isForgetPassword ? otp.equals(userOtp.getForgetPasswordOtp()) : otp.equals(userOtp.getEmailOtp());
//        }
//        return false;
//    }

	public boolean sendForgotPasswordOtpForTraveller(String emailOrPhone) {
		TravellerRegistrationEntity user = travellerRepository.findByTravellerEmail(emailOrPhone);
		String message = null;
		if (user == null) {
			user = travellerRepository.findByTravellerMobile(emailOrPhone);
		}
		if (user != null) {
			String otp = generateOTP(true);
			TravellerOTPEntity travellerOTPEntity = new TravellerOTPEntity();
			travellerOTPEntity.setForgetPasswordOtp(otp);
			if (emailOrPhone.contains("@")) {
				travellerOTPEntity.setUserEmail(user.getTravellerEmail());

				EmailTemplate template = new EmailTemplate("static/send-otp.html");
				Map<String, String> replacements = new HashMap<>();

				replacements.put("user", user.getTravellerEmail());
				replacements.put("otp", otp);
				message = template.getTemplate(replacements);
			} else {
				travellerOTPEntity.setUserPhoneNumber(user.getTravellerMobile());
			}
			travellerOTPEntity.setTravellerId(user.getTravellerId());
			travellerOTPEntity.setForgotPasswordVerified(false);
			travellerOTPRepo.save(travellerOTPEntity);

			return emailOrPhone.contains("@")
					? emailService.sendEmailMessage(user.getTravellerEmail(), message, "OTP Form Carstand")
					: smsHandler.sendSMSMessage(user.getTravellerMobile(), otp);
		}
		return false;
	}

}
