package com.my.fl.startup.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.my.fl.startup.entity.CabVerificationFeedbackEntity;

@Repository
public interface CabVerificationFeedbackRepo extends JpaRepository<CabVerificationFeedbackEntity, Long> {

	Optional<CabVerificationFeedbackEntity> findByCabGenId(String cabGenId);

	List<CabVerificationFeedbackEntity> findByVerificationFeedback(String status);

}
