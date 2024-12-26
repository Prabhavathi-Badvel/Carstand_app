package com.my.fl.startup.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.my.fl.startup.config.AWSConfig;
import com.my.fl.startup.entity.enums.BookingStatus;
import com.my.fl.startup.entity.enums.Status;
import com.my.fl.startup.exception.ResourceNotFoundException;
import com.my.fl.startup.model.AddDriverModel;
import com.my.fl.startup.model.DriverBookingModel;
import com.my.fl.startup.model.DriverBookingResponse;
import com.my.fl.startup.model.TravellerRegistrationResponse;
import com.my.fl.startup.repo.AddDriverRepo;
import com.my.fl.startup.repo.DriverBookingRepo;
import com.my.fl.startup.repo.TravellerRepository;
import com.my.fl.startup.service.EmailService;
import com.my.fl.startup.utility.PaginatedResponse;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DriverService {

	@Autowired
	DriverBookingRepo bookingRepo;

	@Autowired
	AddDriverRepo addDriverRepo;

	@Autowired
	AWSConfig awsConfig;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private TravellerRepository travellerRepository;

	@Autowired
	private EmailService emailService;

	public PaginatedResponse<DriverBookingModel> getBookings(Integer parsePageNo, Integer parsePageSize,
			String travellerId, Integer seqDriverBookingId, BookingStatus bookingStatus, String fromLocation,
			String toLocation, LocalDate fromDate, LocalDate toDate, LocalDate pickUpDate, String phoneNumber) {
		List<DriverBookingModel> bookingsModel = new ArrayList<DriverBookingModel>();

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		// Main query for fetching data
		CriteriaQuery<DriverBooking> criteriaQuery = criteriaBuilder.createQuery(DriverBooking.class);
		Root<DriverBooking> root = criteriaQuery.from(DriverBooking.class);

		List<Predicate> predicates = this.getPredicatesFilterForBookDriver(criteriaBuilder, root, travellerId,
				seqDriverBookingId, bookingStatus, fromLocation, toLocation, fromDate, toDate, pickUpDate, phoneNumber);

		Pageable pageable = PageRequest.of(parsePageNo, parsePageSize);
		criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0]))).distinct(true);

		// Fetch as per limit
		List<DriverBooking> driverPreferedRouteList = entityManager.createQuery(criteriaQuery)
				.setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize()).getResultList();

		// Create count query with a new root
		CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
		Root<DriverBooking> countRoot = countQuery.from(DriverBooking.class);

		// Get list of predicates for count query
		List<Predicate> countPredicate = this.getPredicatesFilterForBookDriver(criteriaBuilder, countRoot, travellerId,
				seqDriverBookingId, bookingStatus, fromLocation, toLocation, fromDate, toDate, pickUpDate, phoneNumber);

		countQuery.select(criteriaBuilder.count(countRoot))
				.where(criteriaBuilder.and(countPredicate.toArray(new Predicate[0]))).distinct(true);

		// Fetch count of contents
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		Page<DriverBooking> pageData = new PageImpl<>(driverPreferedRouteList, pageable, count);

		// Build the paginated response
		PaginatedResponse<DriverBookingModel> paginatedResponse = new PaginatedResponse<>();
		for (DriverBooking booking : driverPreferedRouteList) {
			DriverBookingModel obj = new DriverBookingModel();
			obj.setBookingDate(booking.getBookingDate());
			obj.setBookingId(booking.getBookingId());
			obj.setBookingStatus(booking.getBookingStatus());
			obj.setContactNo(booking.getContactNo());
			obj.setDriverEmail(booking.getDriverEmail());
			obj.setDriverId(booking.getDriverId());
			obj.setDriverRouteId(booking.getDriverRouteId());
			obj.setDroppingAddress(booking.getDroppingAddress());
			obj.setFromCity(booking.getFromCity());
			obj.setPickUpTime(booking.getPickUpTime());
			obj.setPickUpDate(booking.getPickUpDate());
			obj.setPickUpAddress(booking.getPickUpAddress());
			obj.setSeqDriverBookingId(booking.getSeqDriverBookingId());
			obj.setToCity(booking.getToCity());
			obj.setUserEmail(booking.getUserEmail());
			obj.setTravellerId(booking.getTravellerId());
			bookingsModel.add(obj);
		}
		paginatedResponse.setItems(bookingsModel);
		paginatedResponse.setPageNumber(pageData.getNumber());
		paginatedResponse.setPageSize(pageData.getSize());
		paginatedResponse.setTotalElements(count);
		paginatedResponse.setTotalPages(pageData.getTotalPages());

		return paginatedResponse;
	}

	public ResponseEntity<?> getConfirmationBookings() {
		List<DriverBookingModel> bookings = new ArrayList<DriverBookingModel>();
		List<DriverBooking> list = bookingRepo.findByBookingStatus(BookingStatus.CONFIRMED);
		if (list != null) {
			for (DriverBooking booking : list) {
				DriverBookingModel obj = new DriverBookingModel();
				obj.setBookingDate(booking.getBookingDate());
				obj.setBookingId(booking.getBookingId());
				obj.setBookingStatus(booking.getBookingStatus());
				obj.setContactNo(booking.getContactNo());
				obj.setDriverEmail(booking.getDriverEmail());
				obj.setDriverId(booking.getDriverId());
				obj.setDriverRouteId(booking.getDriverRouteId());
				obj.setDroppingAddress(booking.getDroppingAddress());
				obj.setFromCity(booking.getFromCity());
				obj.setPickUpTime(booking.getPickUpTime());
				obj.setPickUpDate(booking.getPickUpDate());
				obj.setPickUpAddress(booking.getPickUpAddress());
				obj.setSeqDriverBookingId(booking.getSeqDriverBookingId());
				obj.setToCity(booking.getToCity());
				obj.setUserEmail(booking.getUserEmail());
				bookings.add(obj);
			}
		}
		return new ResponseEntity<List<DriverBookingModel>>(bookings, HttpStatus.OK);
	}

	public ResponseEntity<?> getCancelationBookings() {
		List<DriverBookingModel> bookings = new ArrayList<DriverBookingModel>();
		List<DriverBooking> list = bookingRepo.findByBookingStatus(BookingStatus.CANCELLED);
		if (list != null) {
			for (DriverBooking booking : list) {
				DriverBookingModel obj = new DriverBookingModel();
				obj.setBookingDate(booking.getBookingDate());
				obj.setBookingId(booking.getBookingId());
				obj.setBookingStatus(booking.getBookingStatus());
				obj.setContactNo(booking.getContactNo());
				obj.setDriverEmail(booking.getDriverEmail());
				obj.setDriverId(booking.getDriverId());
				obj.setDriverRouteId(booking.getDriverRouteId());
				obj.setDroppingAddress(booking.getDroppingAddress());
				obj.setFromCity(booking.getFromCity());
				obj.setPickUpTime(booking.getPickUpTime());
				obj.setPickUpDate(booking.getPickUpDate());
				obj.setPickUpAddress(booking.getPickUpAddress());
				obj.setSeqDriverBookingId(booking.getSeqDriverBookingId());
				obj.setToCity(booking.getToCity());
				obj.setUserEmail(booking.getUserEmail());
				bookings.add(obj);
			}
		}
		return new ResponseEntity<List<DriverBookingModel>>(bookings, HttpStatus.OK);
	}

	public ResponseEntity<?> searchDriver(String city) {
		List<AddDriverModel> response = new ArrayList<AddDriverModel>();
		List<AddDriverEntity> addDriverEntities = addDriverRepo.findByCityAndAvailability(city, "AVAILABLE");

		for (AddDriverEntity addDriverEntity : addDriverEntities) {
			AddDriverModel addDriverModel = new AddDriverModel();
			addDriverModel.setSeqDriverId(addDriverEntity.getSeqDriverId());
			addDriverModel.setFirstName(addDriverEntity.getFirstName());
			addDriverModel.setLastName(addDriverEntity.getLastName());
			addDriverModel.setEmail(addDriverEntity.getEmail());
			addDriverModel.setMobileNo(addDriverEntity.getMobileNo());
			addDriverModel.setDob(addDriverEntity.getDob());
			addDriverModel.setAddress(addDriverEntity.getAddress());
			addDriverModel.setStreet(addDriverEntity.getStreet());
			addDriverModel.setCity(addDriverEntity.getCity());
			addDriverModel.setState(addDriverEntity.getState());
			addDriverModel.setDistrict(addDriverEntity.getDistrict());
			addDriverModel.setPincode(addDriverEntity.getPincode());
			addDriverModel.setRegisteredState(addDriverEntity.getRegisteredState());
			addDriverModel.setLicenseNo(addDriverEntity.getLicenseNo());
			addDriverModel.setLicenseType(addDriverEntity.getLicenseType());
			addDriverModel.setExpiryDate(addDriverEntity.getExpiryDate());
			addDriverModel.setDrivingExp(addDriverEntity.getDrivingExp());
			addDriverModel.setPermitType(addDriverEntity.getPermitType());
			addDriverModel.setWithinRange(addDriverEntity.getWithinRange());
			addDriverModel.setAdharNo(addDriverEntity.getAdharNo());
			addDriverModel.setPanNo(addDriverEntity.getPanNo());
			addDriverModel.setLicenseDoc(awsConfig.getUrl(addDriverEntity.getLicenseDoc()));
			addDriverModel.setAdharDoc(awsConfig.getUrl(addDriverEntity.getAdharDoc()));
			addDriverModel.setInsuranceDoc(awsConfig.getUrl(addDriverEntity.getInsuranceDoc()));
			addDriverModel.setRcDoc(awsConfig.getUrl(addDriverEntity.getRcDoc()));
			addDriverModel.setPhoto(awsConfig.getUrl(addDriverEntity.getPhoto()));
			addDriverModel.setMUserId(addDriverEntity.getMUserId());
			addDriverModel.setAvailability(addDriverEntity.getAvailability());
			addDriverModel.setDriverId(addDriverEntity.getDriverId());
			addDriverModel.setStatus(Status.valueOf(addDriverEntity.getStatus()));
			addDriverModel.setJobType(addDriverEntity.getJobType());
			addDriverModel.setRegisteredDate(addDriverEntity.getRegisteredDate());
			response.add(addDriverModel);
		}

		return new ResponseEntity<List<AddDriverModel>>(response, HttpStatus.OK);
	}

	@Transactional
	public Object changeDriverBookingStatus(String travellerId, Integer seqDriverBookingId,
			BookingStatus bookingStatus) {
		DriverBooking driverBooking = bookingRepo.findBySeqDriverBookingIdAndTravellerId(seqDriverBookingId,
				travellerId);

		AddDriverEntity driverEntity = addDriverRepo.findByDriverId(driverBooking.getDriverId());
		
		if (driverBooking == null) {
			throw new IllegalArgumentException("No driver booking found");
		}
		TravellerRegistrationEntity tr = travellerRepository.findById(Long.parseLong(driverBooking.getTravellerId()))
				.orElseThrow(
						() -> new ResourceNotFoundException(" User Not Found By : " + driverBooking.getTravellerId()));
		driverBooking.setBookingStatus(bookingStatus);
		// TODO Sending Email Customer
		String message = String.format("Dear %s,\n\n"
				+ "We wanted to inform you that your booking with ID #%d has been updated.\n"
				+ "The new status of your booking is: %s.\n\n"
				+ "Thank you for choosing our service. If you have any questions, please feel free to contact us.\n\n"
				+ "Best regards,\n" + "www.carstand.in,\n" + "Kosuri engineers pvt ltd,\n" + "info@kosuriers.com",
				tr.getTravellerName(), seqDriverBookingId, bookingStatus.toString());
		String sub = "Your Booking Status Has Been Updated";
		emailService.sendEmailMessage(tr.getTravellerEmail(), message, sub);

		// TODO Sending Email Driver
		String driverSubject = "A Booking Assigned to You Has Been Updated";

		// Create the message body for the driver
		String driverMessage = String.format(
				"Dear %s,\n\n"
						+ "We wanted to inform you that a booking assigned to you with ID #%d has been updated.\n"
						+ "The new status of this booking is: %s.\n\n"
						+ "Please contact the traveler if needed. Here are their details:\n" + "Name: %s\n"
						+ "Thank you for your continued service.\n\n" + "Best regards,\n" + "www.carstand.in,\n"
						+ "Kosuri Engineers Pvt Ltd,\n" + "info@kosuriers.com",
						driverEntity.getFirstName()+" "+driverEntity.getLastName(), seqDriverBookingId, bookingStatus.toString(), tr.getTravellerName());
		emailService.sendEmailMessage(driverBooking.getDriverEmail(), driverMessage, driverSubject);
		// TODO Sending SMS
		return bookingRepo.save(driverBooking);
	}

	private List<Predicate> getPredicatesFilterForBookDriver(CriteriaBuilder criteriaBuilder, Root<DriverBooking> root,
			String travellerId, Integer seqDriverBookingId, BookingStatus bookingStatus, String fromLocation,
			String toLocation, LocalDate fromDate, LocalDate toDate, LocalDate pickUpDate, String phoneNumber) {

		List<Predicate> predicates = new ArrayList<>();

		if (travellerId != null && !travellerId.isEmpty()) {
			predicates.add(criteriaBuilder.equal(root.get("travellerId"), travellerId));
		}
		if (fromLocation != null && !fromLocation.isEmpty()) {
			predicates.add(criteriaBuilder.equal(root.get("fromLocation"), fromLocation));
		}
		if (toLocation != null && !toLocation.isEmpty()) {
			predicates.add(criteriaBuilder.equal(root.get("toLocation"), toLocation));
		}
		if (phoneNumber != null && !phoneNumber.isEmpty()) {
			predicates.add(criteriaBuilder.equal(root.get("phoneNumber"), phoneNumber));
		}
		if (seqDriverBookingId != null) {
			predicates.add(criteriaBuilder.equal(root.get("seqDriverBookingId"), seqDriverBookingId));
		}
		if (bookingStatus != null) {
			predicates.add(criteriaBuilder.equal(root.get("bookingStatus"), bookingStatus));
		}

		// Filter by pickUpDate range if provided
		if (fromDate != null && toDate != null) {
			predicates.add(criteriaBuilder.between(root.get("pickUpDate"), fromDate, toDate));
		} else if (fromDate != null) {
			predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("pickUpDate"), fromDate));
		} else if (toDate != null) {
			predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("pickUpDate"), toDate));
		}

		// Optional filter by pickUpDate if provided
		// This should be in addition to the range filter if a specific date is required
		if (pickUpDate != null) {
			predicates.add(criteriaBuilder.equal(root.get("pickUpDate"), pickUpDate));
		}

		return predicates;
	}

	public List<DriverBookingResponse> searchBookingByDriver(String fromCity, String toCity, LocalDate fromBookingDate,
			LocalDate toBookingDate, LocalDate fromPickupDate, LocalDate toPickupDate, String travellerPhone) {
		log.info(">>searchBookingByDriver({}, {}, {}, {}, {}, {}, {})", fromCity, toCity, fromBookingDate,
				toBookingDate, fromPickupDate, toPickupDate, travellerPhone);

		if (travellerPhone != null) {
			List<DriverBookingResponse> dbrs = new ArrayList<>();
			TravellerRegistrationEntity tr = travellerRepository.findByTravellerMobile(travellerPhone);
			if (tr != null) {
				List<DriverBooking> driverBookings = bookingRepo.findAllByTravellerId(tr.getTravellerId().toString());
				driverBookings.stream().forEach(db -> {
					TravellerRegistrationResponse trres = mapper.map(tr, TravellerRegistrationResponse.class);
					DriverBookingResponse dbres = mapper.map(db, DriverBookingResponse.class);
					dbres.setTravellerRegistrationResponse(trres);
					dbrs.add(dbres);
				});
			} else {
				return Collections.emptyList();
			}
			return dbrs;
		}

		List<DriverBooking> driverBookings = bookingRepo.findAllDriverBookings(fromCity, toCity, fromBookingDate,
				toBookingDate, fromPickupDate, toPickupDate);
		return driverBookings.stream().map(booking -> {
			DriverBookingResponse dbr = mapper.map(booking, DriverBookingResponse.class);
			Long trId = booking.getTravellerId() != null ? Long.parseLong(booking.getTravellerId()) : null;
			if (trId != null) {
				travellerRepository.findById(trId).ifPresent(traveller -> {
					TravellerRegistrationResponse tr = mapper.map(traveller, TravellerRegistrationResponse.class);
					dbr.setTravellerRegistrationResponse(tr);
				});
			} else {
				log.warn("Traveller ID is null for booking ID {}", booking.getTravellerId());
			}

			return dbr;
		}).toList();
	}

}
