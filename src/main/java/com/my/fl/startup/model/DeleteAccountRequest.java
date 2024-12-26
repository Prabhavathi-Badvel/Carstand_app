package com.my.fl.startup.model;

import lombok.Data;

@Data
public class DeleteAccountRequest {
	private String userId;
	private String reason;

}
