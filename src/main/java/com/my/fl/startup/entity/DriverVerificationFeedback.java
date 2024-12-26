package com.my.fl.startup.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "driver_verification_feedback")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DriverVerificationFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DR_FD_SEQ_NO")
    private Long id;

    @Column(name = "DRIVER_EMAIL")
    private String driverEmail;

    @Column(name = "FEED_BACK")
    private String feedback;

    @Column(name = "ADMIN_EMAIL")
    private String adminEmail;

    @Column(name = "DRIVER_ID")
    private String driverId;

    @Column(name = "FEED_BACK_DATE")
    private LocalDateTime feedbackDate;

}
