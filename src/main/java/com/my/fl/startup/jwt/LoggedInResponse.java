package com.my.fl.startup.jwt;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class LoggedInResponse {

	private String accessToken;
	private String tokenType = "Bearer";
	private String username;
	private String userId;
	private String loggedInContact;
	private String verifiedMobile;
	private String name;
	private String email;
	private String roleName;
	private Boolean isVerified;
	private String status;
	private String errorMsg;

	public LoggedInResponse(String accessToken,  String username, String userId,
			String loggedInContact, String name, String email, String roleName, Boolean isVerified, String status) {
		super();
		this.accessToken = accessToken;
		this.username = username;
		this.userId = userId;
		this.loggedInContact = loggedInContact;
		this.name = name;
		this.email = email;
		this.roleName = roleName;
		this.isVerified = isVerified;
		this.status = status;
	}

}