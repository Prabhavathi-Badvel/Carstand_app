package com.my.fl.startup.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.my.fl.startup.entity.AddCab;
import com.my.fl.startup.entity.CabVerificationFeedbackEntity;
import com.my.fl.startup.entity.RegistrationEntity;
import com.my.fl.startup.repo.CabRepo;
import com.my.fl.startup.repo.CabVerificationFeedbackRepo;
import com.my.fl.startup.repo.RegistrationRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CabReportService {

	@Autowired
	private RegistrationRepository registrationRepository;

	@Autowired
	private CabRepo cabRepo;

	@Autowired
	private CabVerificationFeedbackRepo verificationFeedRepo;

	@Transactional
	public List<AddCab> getCabReports(LocalDateTime fromRegDate, LocalDateTime toRegDate, String mobile, String email,
			String brand, String status) {
		log.info(">>getUserById({})", email);
		List<RegistrationEntity> users = registrationRepository.findAllData(fromRegDate, toRegDate, mobile, email);
		if (!users.isEmpty()) {
			return users.stream().flatMap(user -> cabRepo.findByCabOwnerId(user.getCandidateID()).stream())
					.collect(Collectors.toList());
		} else if (brand != null) {
			return cabRepo.findByCabBrand(brand);
		} else if (status != null) {
			List<CabVerificationFeedbackEntity> feedbackStatus = verificationFeedRepo
					.findByVerificationFeedback(status);
			if (!feedbackStatus.isEmpty()) {
				return feedbackStatus.stream()
						.flatMap(verifydata -> cabRepo.findAllByCabGenId(verifydata.getCabGenId()).stream())
						.collect(Collectors.toList());
			}
		}
		return cabRepo.findAll();

	}

}
