package com.my.fl.startup.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.my.fl.startup.entity.TravellerRegistrationEntity;

import jakarta.transaction.Transactional;

@Repository
public interface TravellerRepository extends JpaRepository<TravellerRegistrationEntity, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE TravellerRegistrationEntity r SET r.status = :status, r.travellerEmailVerified = :emailVerified WHERE r.travellerEmail = :travellerEmail")
    void updateStatusAndVerificationByEmail(@Param("status") String status, @Param("emailVerified") String emailVerified, @Param("travellerEmail") String travellerEmail);

    @Transactional
    @Modifying
    @Query("UPDATE TravellerRegistrationEntity r SET r.status = :status, r.travellerMobileVerified = :mobileVerified WHERE r.travellerMobile = :mobileNumber")
    void updateStatusAndVerificationByMobileNumber(@Param("status") String status, @Param("mobileVerified") String mobileVerified,  @Param("mobileNumber") String mobileNumber);


    TravellerRegistrationEntity findByTravellerEmail(String email);

    TravellerRegistrationEntity findByTravellerMobile(String phoneNumber);

    TravellerRegistrationEntity findByTravellerEmailOrTravellerMobile(String travellerEmail, String travellerMobile);
}

