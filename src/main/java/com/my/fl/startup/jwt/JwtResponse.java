package com.my.fl.startup.jwt;

//import com.scm.core.model.BusinessRoleModel;

public class JwtResponse {
	private String token;
	private String type = "Bearer";
	private String username;
	private String userId;
	private String businessId;
	private String name;
	private String email;
	private String roleName;
	private Boolean isVerified;
//	private BusinessRoleModel businessRoleModel;
	private String status;
	private String errorMsg;

	public JwtResponse(String accessToken, String username, String userId, String businessId, String name, String email,
			String roleName, Boolean isVerified, String status, String errorMsg) {
		this.token = accessToken;
		this.username = username;
		this.userId = userId;
		this.name = name;
		this.email = email;
		this.businessId = businessId;
		this.roleName = roleName;
		this.isVerified = isVerified;
		this.status = status;
		this.errorMsg = errorMsg;

	}

	public String getAccessToken() {
		return token;
	}

	public void setAccessToken(String accessToken) {
		this.token = accessToken;
	}

	public String getTokenType() {
		return type;
	}

	public void setTokenType(String tokenType) {
		this.type = tokenType;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}