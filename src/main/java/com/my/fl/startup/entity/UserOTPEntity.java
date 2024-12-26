package com.my.fl.startup.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_otp")
public class UserOTPEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@Nonnull
	private @Column(name = "user_id") String userOtpId;
	private @Column(name = "active") Integer active;
	private @Column(name = "created_on") String createdOn;
	private @Column(name = "updated_on") String updatedOn;
	private @Column(name = "email") String userEmail;
	private @Column(name = "email_otp_date") Date emailOtpDate;
	private @Column(name = "phone_number") String userPhoneNumber;
	private @Column(name = "phone_otp_date") Date phoneOtpDate;
	private @Column(name = "email_otp") String emailOtp;
	private @Column(name = "phone_otp") String phoneOtp;
	private @Column(name = "email_verify") boolean emailVerify;
	private @Column(name = "sms_verify") boolean smsVerify;
	private @Column(name = "forget_password_otp") String forgetPasswordOtp;

	@PrePersist
	public void createdOn() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		setCreatedOn(dateFormat.format(new Date()));
	}
}