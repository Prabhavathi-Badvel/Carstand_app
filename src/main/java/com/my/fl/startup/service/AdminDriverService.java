package com.my.fl.startup.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.my.fl.startup.entity.AddDriverEntity;
import com.my.fl.startup.entity.DriverVerificationFeedback;
import com.my.fl.startup.entity.enums.Status;
import com.my.fl.startup.entity.enums.VericationStatus;
import com.my.fl.startup.model.ResponseModel;
import com.my.fl.startup.repo.AddDriverRepo;
import com.my.fl.startup.repo.DriverVerificationFeedbackRepo;
import com.my.fl.startup.utils.AuthDetailsProvider;

@Service
public class AdminDriverService {

	@Autowired
	private AddDriverRepo addDriverRepo;

	@Autowired
	private DriverVerificationFeedbackRepo driverVerificationFeedbackRepo;

	@Transactional
	public ResponseModel verifyDriver(String driverId, VericationStatus status) {
		ResponseModel rs = new ResponseModel();
		String logginUserEmai = AuthDetailsProvider.getLoggedEmail();
		AddDriverEntity driver = addDriverRepo.findByDriverId(driverId);
		if (driver == null) {
			rs.setError("true");
			rs.setMsg("Driver Details not found.");
			return rs;
		}
		DriverVerificationFeedback dvf = driverVerificationFeedbackRepo.findByDriverId(driver.getDriverId());
		if (dvf == null) {
			dvf = new DriverVerificationFeedback();
		}
		if (VericationStatus.VERIFIED.equals(status)) {
			driver.setStatus(Status.ACTIVE.toString());
		} else {
			driver.setStatus(Status.INACTIVE.toString());
		}

		dvf.setDriverEmail(driver.getEmail());
		dvf.setDriverId(driver.getDriverId());
		dvf.setAdminEmail(logginUserEmai);
		dvf.setFeedback(status.toString());
		dvf.setFeedbackDate(LocalDateTime.now());
		driverVerificationFeedbackRepo.save(dvf);

		rs.setError("false");
		rs.setMsg("successfully update status. " + status);
		return rs;
	}

}
