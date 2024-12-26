package com.my.fl.startup.repo;

import com.my.fl.startup.entity.UserOTPEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserOTPRepository extends JpaRepository<UserOTPEntity, Long>  {

	Optional<UserOTPEntity> findByUserEmail(String email);

	Optional<UserOTPEntity> findByUserEmailAndActive(String email, Integer isActive);

	Optional<UserOTPEntity> findByUserPhoneNumber(String phoneNumber);

	Optional<UserOTPEntity> findByUserPhoneNumberAndUserEmail(String phoneNumber, String email);

	Optional<UserOTPEntity> findFirstByUserEmailOrderByCreatedOnAsc(String userEmail);

	@Query("SELECT u FROM UserOTPEntity u WHERE u.userEmail = :email AND (u.emailOtp = :otp OR u.forgetPasswordOtp = :otp)")
	UserOTPEntity findByUserEmailAndEmailOtpOrForgetEmailOtp(String email, String otp);

	@Query("SELECT u FROM UserOTPEntity u WHERE u.userPhoneNumber = :phoneNumber AND (u.phoneOtp = :otp OR u.forgetPasswordOtp = :otp)")
	UserOTPEntity findByUserPhoneNumberAndPhoneOtpOrForgetEmailOtp(String phoneNumber, String otp);

	Optional<UserOTPEntity> findFirstByUserPhoneNumberOrderByCreatedOnAsc(String emailOrPhone);

	Optional<UserOTPEntity> findByUserOtpId(String travellerId);
}
