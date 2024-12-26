package com.my.fl.startup.utils;

import org.springframework.stereotype.Service;

import com.my.fl.startup.jwt.JwtProvider;

@Service
public class AuthDetailsProvider {

	public static String getLoggedEmail() {
		return JwtProvider.CURRENT_USER;
	}

}
