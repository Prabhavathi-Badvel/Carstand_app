package com.my.fl.startup.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.my.fl.startup.entity.AddDriverEntity;
import com.my.fl.startup.entity.DriverPreferedRoute;
import com.my.fl.startup.entity.DriverVerificationFeedback;
import com.my.fl.startup.model.DriverPreferedRouteModel;
import com.my.fl.startup.model.DriverPreferedRouteRequest;
import com.my.fl.startup.model.PaginatedResponse;
import com.my.fl.startup.model.ResponseModel;
import com.my.fl.startup.repo.AddDriverRepo;
import com.my.fl.startup.repo.DriverPreferedRouteRepo;
import com.my.fl.startup.repo.DriverVerificationFeedbackRepo;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class DriverPreferedRouteService {

	@Autowired
	DriverPreferedRouteRepo driverPreferedRouteRepo;

	@Autowired
	DriverVerificationFeedbackRepo driverVerificationFeedbackRepo;

	@Autowired
	private AddDriverRepo addDriverRepo;

	@PersistenceContext
	private EntityManager entityManager;

	public ResponseModel addDriverRoute(DriverPreferedRouteRequest request) {
		DriverPreferedRoute entity = new DriverPreferedRoute();
		ResponseModel response = new ResponseModel();
		try {

			DriverVerificationFeedback feedback = driverVerificationFeedbackRepo.findByDriverId(request.getDriverId());

			if (feedback != null && feedback.getFeedback().equals("VERIFIED")) {
				if (request.getId() != null) {
					entity = driverPreferedRouteRepo.findById(request.getId()).get();
				} else {

					Random random = new Random();

					int randomNo = random.nextInt(90) + 10;
					entity.setDriverRouteId("ZORT" + randomNo);
				}
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
				LocalDate availableDate = LocalDate.parse(request.getAvailableDate(), formatter);

				entity.setFromCity(request.getFromCity());
				entity.setToCity(request.getToCity());
				entity.setAvailableDate(availableDate);
				entity.setAvailableTime(request.getAvailableTime());
				entity.setRouteStatus(request.getRouteStatus());
				entity.setDriverEmail(request.getDriverEmail());
				entity.setDriverId(request.getDriverId());
				entity.setRouteAddedDate(LocalDateTime.now());

				driverPreferedRouteRepo.save(entity);
				response.setError("false");
				response.setMsg("Added Successfully");
				response.setPreferedRoute(entity);
			} else {
				response.setError("true");
				response.setMsg("This driver is not yet verified by admin, please allow us some time to verify");
			}

		} catch (Exception e) {
			// TODO: handle exception
			response.setError("true");
			response.setMsg("Something went wrong");
			e.printStackTrace();
		}

		return response;
	}

	public PaginatedResponse<DriverPreferedRouteModel> getDriverPreferedRoute(Integer parsePageNo,
			Integer parsePageSize, String mUserId, String fromCity, String toCity, List<String> diverId) {
		List<DriverPreferedRouteModel> routeModelList = new ArrayList<>();
		PaginatedResponse<DriverPreferedRouteModel> paginatedResponse = new PaginatedResponse<>();

		try {
			// first get driver id's by muser id , then find routes by those driver id's
			if (mUserId != null && !mUserId.isEmpty()) {
				List<AddDriverEntity> addDriverEntityList = addDriverRepo.findByMUserId(mUserId);

				if (addDriverEntityList != null && !addDriverEntityList.isEmpty()) {
					List<String> driverIdList = addDriverEntityList.stream().map(AddDriverEntity::getDriverId) // Use
																												// method
																												// reference
																												// for
																												// clarity
							.filter(Objects::nonNull) // Optional: Filter out null driver IDs
							.collect(Collectors.toList());

					if (!driverIdList.isEmpty()) {
						if (diverId == null || diverId.isEmpty()) {
							diverId = new ArrayList<>();
							diverId.addAll(driverIdList);
						} else {
							diverId.addAll(driverIdList);
						}
					}
				}
			}
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

			// Main query for fetching data
			CriteriaQuery<DriverPreferedRoute> criteriaQuery = criteriaBuilder.createQuery(DriverPreferedRoute.class);
			Root<DriverPreferedRoute> root = criteriaQuery.from(DriverPreferedRoute.class);

			List<Predicate> predicates = this.getPredicatesFilterForDriver(criteriaBuilder, root, fromCity, toCity,
					diverId);

			Pageable pageable = PageRequest.of(parsePageNo, parsePageSize);
			criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0]))).distinct(true);

			// Fetch as per limit
			List<DriverPreferedRoute> driverPreferedRouteList = entityManager.createQuery(criteriaQuery)
					.setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize()).getResultList();

			// Create count query with a new root
			CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
			Root<DriverPreferedRoute> countRoot = countQuery.from(DriverPreferedRoute.class);

			// Get list of predicates for count query
			List<Predicate> countPredicate = this.getPredicatesFilterForDriver(criteriaBuilder, countRoot, fromCity,
					toCity, diverId);

			countQuery.select(criteriaBuilder.count(countRoot))
					.where(criteriaBuilder.and(countPredicate.toArray(new Predicate[0]))).distinct(true);

			// Fetch count of contents
			Long count = entityManager.createQuery(countQuery).getSingleResult();

			Page<DriverPreferedRoute> pageData = new PageImpl<>(driverPreferedRouteList, pageable, count);

			for (DriverPreferedRoute entity : driverPreferedRouteList) {
				DriverPreferedRouteModel routeModel = new DriverPreferedRouteModel();
				routeModel.setId(entity.getId());
				routeModel.setFromCity(entity.getFromCity());
				routeModel.setToCity(entity.getToCity());
				routeModel.setAvailableDate(entity.getAvailableDate());
				routeModel.setAvailableTime(entity.getAvailableTime());
				routeModel.setRouteStatus(entity.getRouteStatus());
				routeModel.setDriverEmail(entity.getDriverEmail());
				routeModel.setDriverId(entity.getDriverId());
				routeModel.setRouteAddedDate(entity.getRouteAddedDate());
				routeModel.setDriverRouteId(entity.getDriverRouteId());
				routeModelList.add(routeModel);

			}
			// Build the paginated response
			paginatedResponse.setItems(routeModelList);
			paginatedResponse.setPageNumber(pageData.getNumber());
			paginatedResponse.setPageSize(pageData.getSize());
			paginatedResponse.setTotalElements(count);
			paginatedResponse.setTotalPages(pageData.getTotalPages());

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return paginatedResponse;
	}

	private List<Predicate> getPredicatesFilterForDriver(CriteriaBuilder criteriaBuilder,
			Root<DriverPreferedRoute> root, String fromCity, String toCity, List<String> driverId) {

		List<Predicate> predicates = new ArrayList<>();

		if (fromCity != null && !fromCity.isEmpty()) {
			predicates.add(criteriaBuilder.equal(root.get("fromCity"), fromCity));
		}
		if (toCity != null && !toCity.isEmpty()) {
			predicates.add(criteriaBuilder.equal(root.get("toCity"), toCity));
		}
		if (!CollectionUtils.isEmpty(driverId)) {
			predicates.add(root.get("driverId").in(driverId));
		}
		return predicates;
	}

	public ResponseModel updateDriverRoute(DriverPreferedRouteModel driverPreferedRouteModel) {
		ResponseModel response = new ResponseModel();

		DriverPreferedRoute entity = driverPreferedRouteRepo.findByDriverIdAndId(driverPreferedRouteModel.getDriverId(),
				driverPreferedRouteModel.getId());

		if (entity == null) {
			response.setError("true");
			response.setMsg("Driver route not found");
			return response;
		}

		if (driverPreferedRouteModel.getAvailableDate() != null) {
			entity.setAvailableDate(driverPreferedRouteModel.getAvailableDate());
		}

		if (driverPreferedRouteModel.getAvailableTime() != null
				&& !driverPreferedRouteModel.getAvailableTime().isEmpty()) {
			entity.setAvailableTime(driverPreferedRouteModel.getAvailableTime());
		}

		if (driverPreferedRouteModel.getFromCity() != null && !driverPreferedRouteModel.getFromCity().isEmpty()) {
			entity.setFromCity(driverPreferedRouteModel.getFromCity());
		}

		if (driverPreferedRouteModel.getToCity() != null && !driverPreferedRouteModel.getToCity().isEmpty()) {
			entity.setToCity(driverPreferedRouteModel.getToCity());
		}

		if (driverPreferedRouteModel.getRouteStatus() != null && !driverPreferedRouteModel.getRouteStatus().isEmpty()) {
			entity.setRouteStatus(driverPreferedRouteModel.getRouteStatus());
		}

		driverPreferedRouteRepo.save(entity);

		response.setError("false");
		response.setMsg("Updated Successfully");
		return response;
	}

}