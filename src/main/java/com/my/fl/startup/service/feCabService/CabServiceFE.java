package com.my.fl.startup.service.feCabService;

import com.my.fl.startup.entity.*;
import com.my.fl.startup.entity.cabBooking.BookingCabRequestEntity;
import com.my.fl.startup.entity.enums.BookingStatus;
import com.my.fl.startup.entity.enums.CabRouteStatus;
import com.my.fl.startup.entity.enums.Status;
import com.my.fl.startup.model.AddDriverModel;
import com.my.fl.startup.model.DriverPreferedRouteResponse;
import com.my.fl.startup.model.traveller.BookDriverRequest;
import com.my.fl.startup.repo.*;
import com.my.fl.startup.repo.cabBooking.BookingCabRequestRepository;
import com.my.fl.startup.service.EmailService;
import com.my.fl.startup.service.SmsHandler;
import com.my.fl.startup.utility.PaginatedResponse;
import com.my.fl.startup.utility.SecurityUtils;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CabServiceFE {

	@Autowired
	private CabRouteRepo cabRouteRepository;

	@Autowired
	private CabRepo addCabRepository;
	@Autowired
	private BookingCabRequestRepository bookingCabRequestRepository;
	@Autowired
	private AddDriverRepo addDriverRepo;
	@Autowired
	private CabBookingRepo cabBookingRepo;
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private DriverPreferedRouteRepo driverPreferedRouteRepo;
	@Autowired
	private DriverBookingRepo driverBookingRepo;
	@Autowired
	AdminEmailSettingsRepo adminEmailSettingsRepo;
	@Autowired
	EmailService emailService;
	@Autowired
	AdminSMSSettings adminSMSSettings;
	@Autowired
	SecurityUtils securityUtils;
	@Autowired
	SmsHandler smsHandler;

	@Autowired
	private ModelMapper modelMapper;

	public PaginatedResponse<AddCab> searchCabs(Integer parsedPageNo, Integer parsedPageSize, String fromLocation,
			String destination, LocalDate fromDate, String vehicleType) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<CabRouteEntity> cq = cb.createQuery(CabRouteEntity.class);
		Root<CabRouteEntity> root = cq.from(CabRouteEntity.class);

		List<Predicate> predicates = new ArrayList<>();
		if (fromLocation != null && !fromLocation.isEmpty()) {
			predicates.add(cb.like(root.get("sourceAddress"), "%" + fromLocation + "%"));
		}
		if (destination != null && !destination.isEmpty()) {
			predicates.add(cb.like(root.get("destination"), "%" + destination + "%"));
		}

		if (fromDate != null) {
			predicates.add(cb.lessThanOrEqualTo(root.get("pickUpDate"), fromDate));
		}

		predicates.add(cb.equal(root.get("status"), CabRouteStatus.AVAILABLE));

		cq.where(predicates.toArray(new Predicate[0]));

		TypedQuery<CabRouteEntity> query = entityManager.createQuery(cq);
		List<CabRouteEntity> result = query.getResultList();

		List<AddCab> addCabs = new ArrayList<>();

		List<String> availableCabGenIds = result.stream().map(CabRouteEntity::getCabId).collect(Collectors.toList());

		PaginatedResponse<AddCab> paginatedResponse = new PaginatedResponse<>();
		Pageable pageable = PageRequest.of(parsedPageNo, parsedPageSize);
		Page<AddCab> page = null;
		if (vehicleType != null && !vehicleType.isEmpty()) {
			page = addCabRepository.findByCabRegNoInAndCabModelAndStatus(availableCabGenIds, vehicleType, Status.ACTIVE,
					pageable);
		} else {
			page = addCabRepository.findByCabRegNoIn(availableCabGenIds, pageable);
		}
		paginatedResponse.setItems(page.getContent());
		paginatedResponse.setPageSize(parsedPageSize);
		paginatedResponse.setPageNumber(parsedPageNo);
		paginatedResponse.setTotalPages(page.getTotalPages());
		paginatedResponse.setTotalElements(page.getTotalElements());
		return paginatedResponse;

	}

	public BookingCabRequestEntity bookCab(BookingCabRequestEntity bookingRequest) throws Exception {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<CabRouteEntity> cq = cb.createQuery(CabRouteEntity.class);
		Root<CabRouteEntity> root = cq.from(CabRouteEntity.class);

		List<Predicate> predicates = new ArrayList<>();
		if (bookingRequest.getSourceAdd() != null && !bookingRequest.getSourceAdd().isEmpty()) {
			predicates.add(cb.like(root.get("sourceAddress"), "%" + bookingRequest.getSourceAdd() + "%"));
		}
		if (bookingRequest.getSourceAdd() != null && !bookingRequest.getSourceAdd().isEmpty()) {
			predicates.add(cb.like(root.get("destination"), "%" + bookingRequest.getDestinationAdd() + "%"));
		}
		if (bookingRequest.getCabRegNo() != null && !bookingRequest.getCabRegNo().isEmpty()) {
			predicates.add(cb.equal(root.get("cabId"), bookingRequest.getCabRegNo()));
		}

		if (bookingRequest.getPickUpDate() != null) {
			predicates.add(cb.lessThanOrEqualTo(root.get("pickUpDate"), bookingRequest.getPickUpDate()));
		}
		predicates.add(cb.equal(root.get("status"), CabRouteStatus.AVAILABLE));

		cq.where(predicates.toArray(new Predicate[0]));
		System.out.println(cq.toString());
		TypedQuery<CabRouteEntity> query = entityManager.createQuery(cq);
		List<CabRouteEntity> result = query.getResultList();

		if (!CollectionUtils.isEmpty(result)) {
			bookingRequest.setBookingDate(LocalDate.now());
			bookingRequest.setBookingTime(LocalTime.now());
			bookingRequest.setBookingStatus(BookingStatus.NEW);
			bookingRequest.setCabRegNo(bookingRequest.getCabRegNo());

			BookingCabRequestEntity savedBookingRequest = bookingCabRequestRepository.save(bookingRequest);
			Long reqNo = savedBookingRequest.getBookingId(); // Get the booking ID after saving
			String userEmail = SecurityUtils.getLoggedInUserEmail();
			AdminEmailSettingsEntity emailDetails = adminEmailSettingsRepo.findById(11L).get();
			String formattedEmailBody = String.format(emailDetails.getEmailBody(), reqNo);
			emailService.sendEmailMessage(userEmail, formattedEmailBody, emailDetails.getEmailSubject());

			String Phone = securityUtils.getLoggedInUserPhoneNumber();
			AdminSMSEntity smsDetails = adminSMSSettings.findById(1L).get();
			String formattedSMS = String.format(smsDetails.getSmsMessage(), reqNo);
			smsHandler.sendCustomMessage(Phone, formattedSMS);

			return savedBookingRequest;
		} else {
			throw new Exception("Cab not available on the given date.");
		}
	}

	public BookingCabRequestEntity cancelCab(BookingCabRequestEntity bookingRequest) throws Exception {
		List<BookingStatus> bookingStatusList = new ArrayList<>();
		bookingStatusList.add(BookingStatus.CONFIRMED);
		bookingStatusList.add(BookingStatus.NEW);
		BookingCabRequestEntity bookingCabRequest = bookingCabRequestRepository
				.findByBookingIdAndBookingStatusIn(bookingRequest.getBookingId(), bookingStatusList);
		if (bookingCabRequest == null) {
			throw new Exception("No booking found");
		}
		bookingCabRequest.setBookingStatus(BookingStatus.CANCELLED);
		bookingCabRequest.setCancelledDate(LocalDate.now());

		Long reqNo = bookingRequest.getBookingId();
		System.out.println("Booking ID: " + bookingCabRequest.getBookingId()); // Debugging statement
		String userEmail = SecurityUtils.getLoggedInUserEmail();
		AdminEmailSettingsEntity emailDetails = adminEmailSettingsRepo.findById(12L).get();
		String formattedEmailBody = String.format(emailDetails.getEmailBody(), reqNo);
		emailService.sendEmailMessage(userEmail, formattedEmailBody, emailDetails.getEmailSubject());

		String Phone = securityUtils.getLoggedInUserPhoneNumber();
		AdminSMSEntity smsDetails = adminSMSSettings.findById(1L).get();
		String formattedSMS = String.format(smsDetails.getSmsMessage(), reqNo);
		smsHandler.sendCustomMessage(Phone, formattedSMS);

		return bookingCabRequestRepository.saveAndFlush(bookingCabRequest);
	}

	public PaginatedResponse<DriverPreferedRoute> searchDriver1(Integer parsePageNo, Integer parsePageSize,
			String fromLocation, String toLocation, LocalDate pickUpDate) {

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// Main query for fetching data
		CriteriaQuery<DriverPreferedRoute> criteriaQuery = criteriaBuilder.createQuery(DriverPreferedRoute.class);
		Root<DriverPreferedRoute> root = criteriaQuery.from(DriverPreferedRoute.class);

		List<Predicate> predicates = this.getPredicatesFilterForDriver(criteriaBuilder, root, fromLocation, toLocation,
				pickUpDate);

		Pageable pageable = PageRequest.of(parsePageNo, parsePageSize);
		criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0]))).distinct(true);

		// Fetch as per limit
		List<DriverPreferedRoute> driverPreferedRouteList = entityManager.createQuery(criteriaQuery)
				.setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize()).getResultList();

		// Create count query with a new root
		CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
		Root<DriverPreferedRoute> countRoot = countQuery.from(DriverPreferedRoute.class);

		// Get list of predicates for count query
		List<Predicate> countPredicate = this.getPredicatesFilterForDriver(criteriaBuilder, countRoot, fromLocation,
				toLocation, pickUpDate);

		countQuery.select(criteriaBuilder.count(countRoot))
				.where(criteriaBuilder.and(countPredicate.toArray(new Predicate[0]))).distinct(true);

		// Fetch count of contents
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		Page<DriverPreferedRoute> pageData = new PageImpl<>(driverPreferedRouteList, pageable, count);
		// TODO driver images return add driver table implement
		// Build the paginated response
		PaginatedResponse<DriverPreferedRoute> paginatedResponse = new PaginatedResponse<>();
		paginatedResponse.setItems(driverPreferedRouteList);
		paginatedResponse.setPageNumber(pageData.getNumber());
		paginatedResponse.setPageSize(pageData.getSize());
		paginatedResponse.setTotalElements(count);
		paginatedResponse.setTotalPages(pageData.getTotalPages());

		return paginatedResponse;
	}

	public PaginatedResponse<DriverPreferedRouteResponse> searchDriver(Integer parsePageNo, Integer parsePageSize,
			String fromLocation, String toLocation, LocalDate pickUpDate) {
		Pageable pageable = PageRequest.of(parsePageNo, parsePageSize);
		Page<DriverPreferedRoute> routes = driverPreferedRouteRepo.findAllDriverPreferedRoutes(fromLocation, toLocation,
				pickUpDate, pageable);
		List<DriverPreferedRouteResponse> routesRes = routes.getContent().stream().map(route -> {
			DriverPreferedRouteResponse response = modelMapper.map(route, DriverPreferedRouteResponse.class);
			AddDriverEntity driver = addDriverRepo.findByDriverId(route.getDriverId());
			AddDriverModel driverRes = modelMapper.map(driver, AddDriverModel.class);
			response.setDriverModel(driverRes);
			return response;
		}).collect(Collectors.toList());

		var pageDto = new PaginatedResponse<DriverPreferedRouteResponse>();
		pageDto.setPageNumber(routes.getNumber());
		pageDto.setPageSize(routes.getSize());
		pageDto.setTotalPages((int) routes.getTotalElements());
		pageDto.setItems(routesRes);
		return pageDto;

	}

	private List<Predicate> getPredicatesFilterForDriver(CriteriaBuilder criteriaBuilder,
			Root<DriverPreferedRoute> root, String fromLocation, String toLocation, LocalDate pickUpDate) {
		List<Predicate> predicates = new ArrayList<>();

		if (pickUpDate != null) {
			// Directly use LocalDate for comparison
			predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("availableDate"), pickUpDate));
		}
		if (fromLocation != null && !fromLocation.isEmpty()) {
			predicates.add(criteriaBuilder.equal(root.get("fromCity"), fromLocation));
		}
		if (toLocation != null && !toLocation.isEmpty()) {
			predicates.add(criteriaBuilder.equal(root.get("toCity"), toLocation));
		}
		return predicates;
	}

	public DriverBooking bookDriver(BookDriverRequest bookDriverRequest) throws ParseException {
		DriverBooking driverBooking = new DriverBooking();

		if (bookDriverRequest.getDriverId() == null || bookDriverRequest.getDriverId().isEmpty()) {
			throw new IllegalArgumentException("DriverId cannot be blank");
		}

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		// Main query for fetching data
		CriteriaQuery<DriverPreferedRoute> criteriaQuery = criteriaBuilder.createQuery(DriverPreferedRoute.class);
		Root<DriverPreferedRoute> root = criteriaQuery.from(DriverPreferedRoute.class);

		List<Predicate> predicates = this.getPredicatesFilterForBookDriver(criteriaBuilder, root, bookDriverRequest);

		criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0]))).distinct(true);

		DriverPreferedRoute driverPreferedRoute = entityManager.createQuery(criteriaQuery).getSingleResult();
		if (driverPreferedRoute != null) {
			driverBooking.setFromCity(driverPreferedRoute.getFromCity());
			driverBooking.setToCity(driverPreferedRoute.getToCity());
			driverBooking.setTravellerId(bookDriverRequest.getTravellerId());
			driverBooking.setPickUpDate(driverPreferedRoute.getAvailableDate());
			driverBooking.setDriverId(driverPreferedRoute.getDriverId());
			driverBooking.setDriverEmail(driverPreferedRoute.getDriverEmail());
			driverBooking.setBookingStatus(BookingStatus.NEW);
			driverBooking.setBookingDate(LocalDate.now());

			driverBookingRepo.save(driverBooking);

			Integer ReqNo = driverBooking.getSeqDriverBookingId();
			String userEmail = SecurityUtils.getLoggedInUserEmail();
			AdminEmailSettingsEntity emailDetails = adminEmailSettingsRepo.findById(9L).get();
			String formattedEmailBody = String.format(emailDetails.getEmailBody(), ReqNo);
			emailService.sendEmailMessage(userEmail, formattedEmailBody, emailDetails.getEmailSubject());
			emailService.sendEmailMessage(driverBooking.getDriverEmail(), formattedEmailBody,
					emailDetails.getEmailSubject());

			String Phone = securityUtils.getLoggedInUserPhoneNumber();
			AdminSMSEntity smsDetails = adminSMSSettings.findById(1L).get();
			String formattedSMS = String.format(smsDetails.getSmsMessage(), ReqNo);
			smsHandler.sendCustomMessage(Phone, formattedSMS);
		}
		return driverBooking;
	}

	public Date convertStringToDate(String dateString) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Adjust format as needed
		return dateFormat.parse(dateString);
	}

	private List<Predicate> getPredicatesFilterForBookDriver(CriteriaBuilder criteriaBuilder,
			Root<DriverPreferedRoute> root, BookDriverRequest bookDriverRequest) {
		List<Predicate> predicates = new ArrayList<>();

		// Handle fromLocation
		if (bookDriverRequest.getFromLocation() != null && !bookDriverRequest.getFromLocation().isEmpty()) {
			predicates.add(criteriaBuilder.equal(root.get("fromCity"), bookDriverRequest.getFromLocation()));
		}

		// Handle toLocation
		if (bookDriverRequest.getToLocation() != null && !bookDriverRequest.getToLocation().isEmpty()) {
			predicates.add(criteriaBuilder.equal(root.get("toCity"), bookDriverRequest.getToLocation()));
		}

		// Handle driverId
		if (bookDriverRequest.getDriverId() != null && !bookDriverRequest.getDriverId().isEmpty()) {
			predicates.add(criteriaBuilder.equal(root.get("driverId"), bookDriverRequest.getDriverId()));
		}

		// Handle pickUpDate
		if (bookDriverRequest.getPickUpDate() != null) {
			predicates.add(
					criteriaBuilder.lessThanOrEqualTo(root.get("availableDate"), bookDriverRequest.getPickUpDate()));
		}
		return predicates;
	}

}
