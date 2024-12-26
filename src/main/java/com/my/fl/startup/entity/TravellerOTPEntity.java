package com.my.fl.startup.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * The type Traveller otp entity.
 */
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "traveller_otp")
public class TravellerOTPEntity extends DateAwareDomain{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "traveller_otp_id")
    private Long travellerOtpId;

    @Column(name = "Traveller_id")
    private Long travellerId;

    @Column(name = "email")
    private String userEmail;

    @Column(name = "phone_number")
    private String userPhoneNumber;

    @Column(name = "forgot_password_otp")
    private String forgotPasswordOtp;

    @Column(name = "forget_password_otp")
    private String forgetPasswordOtp;

    @Column(name = "forgot_password_verified")
    private Boolean forgotPasswordVerified;

}
