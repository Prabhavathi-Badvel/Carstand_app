package com.my.fl.startup.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CabVerificationFeedbackRequest {

	private String cabGenId;
	private String verificationFeedback;
	private VerificationStatus verificationStatus;
	private String adminId;

	public enum VerificationStatus {
		VERIFIED, UNVERIFIED, PENDING;
	}
}
