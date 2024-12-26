package com.my.fl.startup.repo.cabBooking;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.my.fl.startup.entity.cabBooking.BookingCabRequestEntity;
import com.my.fl.startup.entity.enums.BookingStatus;

@Repository
public interface BookingCabRequestRepository extends JpaRepository<BookingCabRequestEntity, Long> {

    BookingCabRequestEntity findByBookingIdAndBookingStatus(Long bookingId, BookingStatus confirmed);

    BookingCabRequestEntity findByBookingIdAndBookingStatusIn(Long bookingId, List<BookingStatus> bookingStatusList);
}
