package com.my.fl.startup.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.my.fl.startup.config.AppProperties;

@Service
public class EmailService {

	private final JavaMailSender javaMailSender;

	private final AppProperties appProperties;

	public EmailService(JavaMailSender javaMailSender, AppProperties appProperties) {
		this.javaMailSender = javaMailSender;
		this.appProperties = appProperties;
	}

	public boolean sendEmailMessage(String email, String mailMessage, String subject) {

		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(email);
			message.setSubject(subject);
			message.setFrom(appProperties.getMailUsername());
			message.setText(mailMessage);
			javaMailSender.send(message);
		} catch (Exception e) {
			System.out.println("Email not sent");
		}
		return true;
	}

}