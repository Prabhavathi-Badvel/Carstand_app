package com.my.fl.startup.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.my.fl.startup.entity.CabBooking;
import com.my.fl.startup.exception.ResourceNotFoundException;
import com.my.fl.startup.model.CabBookingReportModel;
import com.my.fl.startup.model.ResponseModel;
import com.my.fl.startup.model.UpdateStatusRequest;
import com.my.fl.startup.repo.CabBookingRepo;

@Service
public class CabBookingService {

	@Autowired
	CabBookingRepo cabBookingRepo;

	public ResponseEntity<?> cabBookingReport(String cabId, String bookingStatus) {
		List<CabBookingReportModel> bookings = new ArrayList<CabBookingReportModel>();
		List<CabBooking> bookingEntities = new ArrayList<CabBooking>();
		try {
			if (bookingStatus != null) {
				bookingEntities = cabBookingRepo.findByStatus(cabId, bookingStatus);
			} else {
				bookingEntities = cabBookingRepo.findByCabId(cabId);
			}

			for (CabBooking booking : bookingEntities) {
				CabBookingReportModel model = new CabBookingReportModel();
				model.setCabBookingSeqNo(booking.getCabBookingSeqNo());
				model.setUserEmail(booking.getUserEmail());
				model.setContactNo(booking.getContactNo());
				model.setPickUpAddress(booking.getPickUpAddress());
				model.setBookingId(booking.getBookingId());
				model.setBookingDate(booking.getBookingDate());
				model.setBookingStatus(booking.getBookingStatus());
				model.setCabId(booking.getCabId());
				model.setCabOwnerId(booking.getCabOwnerId());
				model.setDroppingAddress(booking.getDroppingAddress());
				model.setCabRouteId(booking.getCabRouteId());
				model.setReturnedDate(booking.getReturnedDate());
				model.setCabServiceType(booking.getCabServiceType());
				model.setTravelingDate(booking.getTravelingDate());
				model.setPickUpTime(booking.getPickUpTime());
				model.setFromCity(booking.getFromCity());
				model.setToCity(booking.getToCity());

				bookings.add(model);
			}

			return new ResponseEntity<List<CabBookingReportModel>>(bookings, HttpStatus.OK);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return new ResponseEntity<ResponseModel>(new ResponseModel("Something went wrong", null),
				HttpStatus.BAD_REQUEST);

	}

	@Transactional
	public Map<String,Object> updateCabStatus(UpdateStatusRequest request) {
		CabBooking bookingEntities = cabBookingRepo.findbyCabId(request.getCabId())
				.orElseThrow(() -> new ResourceNotFoundException("User Not Found By : " + request.getCabId()));
		bookingEntities.setBookingStatus(request.getBookingStatus());
		String mgs = request.getCabId() + " successfully updated " + request.getBookingStatus();
		return Map.of("message", mgs);

	}
}
