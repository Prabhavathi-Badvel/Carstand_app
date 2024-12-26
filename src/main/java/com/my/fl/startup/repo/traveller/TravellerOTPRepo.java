package com.my.fl.startup.repo.traveller;

import com.my.fl.startup.entity.TravellerOTPEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TravellerOTPRepo extends JpaRepository<TravellerOTPEntity,Long> {
 
    TravellerOTPEntity findByUserEmailAndForgotPasswordVerified(String email, boolean b);

    Optional<TravellerOTPEntity> findFirstByUserPhoneNumberAndForgotPasswordVerifiedOrderByLastUpdatedDesc(
            String userPhoneNumber, boolean forgotPasswordVerified);

    Optional<TravellerOTPEntity> findFirstByUserEmailAndForgotPasswordVerifiedOrderByLastUpdatedDesc(
            String userEmail, boolean forgotPasswordVerified);

}
