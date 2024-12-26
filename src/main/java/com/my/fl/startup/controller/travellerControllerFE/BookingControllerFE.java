package com.my.fl.startup.controller.travellerControllerFE;

import com.my.fl.startup.entity.DriverBooking;
import com.my.fl.startup.entity.DriverPreferedRoute;
import com.my.fl.startup.entity.cabBooking.BookingCabRequestEntity;
import com.my.fl.startup.jwt.JwtAuthTokenFilter;
import com.my.fl.startup.jwt.JwtProvider;
import com.my.fl.startup.model.DriverPreferedRouteResponse;
import com.my.fl.startup.model.traveller.BookDriverRequest;
import com.my.fl.startup.service.feCabService.CabServiceFE;
import com.my.fl.startup.utility.PaginatedResponse;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.my.fl.startup.entity.AddCab;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@RestController
@RequestMapping("/fe/api/cab")
public class BookingControllerFE {

	@Autowired
	private CabServiceFE cabServiceFE;
	@Autowired
	private JwtProvider jwtProvider;
	@Autowired
	private JwtAuthTokenFilter jwtAuthTokenFilter;

	@GetMapping("/searchCabs")
	public PaginatedResponse<AddCab> searchCabs(
			@RequestParam(value = "fromLocation", required = false) String fromLocation,
			@RequestParam(value = "destination", required = false) String destination,
			@RequestParam(value = "fromDate", required = false) LocalDate fromDate,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "pageNo", required = false) Integer pageNo,
			@RequestParam(required = false) String vehicleType) {

		Integer parsePageSize = pageSize != null ? pageSize : 10;
		Integer parsePageNo = pageNo != null ? pageNo : 0;
		return cabServiceFE.searchCabs(parsePageNo, parsePageSize, fromLocation, destination, fromDate, vehicleType);
	}

	@PostMapping("/bookCab")
	public ResponseEntity<?> bookCab(@RequestBody BookingCabRequestEntity bookingRequest, HttpServletRequest request) {
		try {
			// Extract JWT token from the request
			String jwt = jwtAuthTokenFilter.getJwt(request);

			// Validate the JWT token
			if (jwtProvider.validateJwtToken(jwt)) {
				// If valid, process the booking request
				BookingCabRequestEntity bookingCabRequest = cabServiceFE.bookCab(bookingRequest);
				return new ResponseEntity<>(bookingCabRequest, HttpStatus.OK);
			} else {
				// Return an unauthorized response if JWT is invalid
				return new ResponseEntity<>("Invalid JWT Token", HttpStatus.UNAUTHORIZED);
			}
		} catch (MalformedJwtException e) {
			// Handle specific JWT exception
			return new ResponseEntity<>("Malformed JWT Token: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			// Handle general exceptions
			return new ResponseEntity<>("Error processing the request: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/cancelCab")
	public BookingCabRequestEntity cancelCab(@RequestBody BookingCabRequestEntity bookingRequest) throws Exception {
		return cabServiceFE.cancelCab(bookingRequest);
	}

	@GetMapping("/searchDriver")
	public PaginatedResponse<DriverPreferedRouteResponse> searchD(
			@RequestParam(value = "fromLocation", required = false) String fromLocation,
			@RequestParam(value = "toLocation", required = false) String toLocation,
			@RequestParam(value = "pickUpDate") LocalDate pickUpDate,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "pageNo", required = false) Integer pageNo) {
		Integer parsePageSize = pageSize != null ? pageSize : 10;
		Integer parsePageNo = pageNo != null ? pageNo : 0;
		return cabServiceFE.searchDriver(parsePageNo, parsePageSize, fromLocation, toLocation, pickUpDate);
	}

	@PostMapping("/bookDriver")
	public ResponseEntity<?> bookDriver(@RequestBody BookDriverRequest bookDriverRequest, HttpServletRequest request) {
		try {
			// Extract JWT token from the request
			String jwt = jwtAuthTokenFilter.getJwt(request);

			// Validate the JWT token
			if (jwtProvider.validateJwtToken(jwt)) {
				// If valid, process the booking request
				DriverBooking driverBooking = cabServiceFE.bookDriver(bookDriverRequest);
				return new ResponseEntity<>(driverBooking, HttpStatus.OK);
			} else {
				// Return an unauthorized response if JWT is invalid
				return new ResponseEntity<>("Invalid JWT Token", HttpStatus.UNAUTHORIZED);
			}
		} catch (MalformedJwtException e) {
			// Handle specific JWT exception
			return new ResponseEntity<>("Malformed JWT Token: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			// Handle general exceptions
			return new ResponseEntity<>("Error processing the request: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
