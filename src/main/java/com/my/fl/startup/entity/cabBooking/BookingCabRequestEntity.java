package com.my.fl.startup.entity.cabBooking;


import com.my.fl.startup.entity.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "booking_cab_request")
public class BookingCabRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "traveller_id")
    private String travellerId;

    @Column(name = "cab_reg_no")
    private String cabRegNo;

    @Column(name = "booking_date")
    private LocalDate bookingDate;
    @Column(name = "booking_time")
    private LocalTime bookingTime;

    @Column(name = "cancel_date")
    private LocalDate cancelledDate;

    @Column(name = "booking_status")
    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    @Column(name = "source_add")
    private String sourceAdd;

    @Column(name = "destination_add")
    private String destinationAdd;

    @Column(name = "pick_up_date")
    private LocalDate pickUpDate;

    @Column(name = "pick_up_time")
    private LocalTime pickUpTime;
}

