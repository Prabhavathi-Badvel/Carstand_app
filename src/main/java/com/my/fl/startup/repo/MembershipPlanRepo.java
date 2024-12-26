package com.my.fl.startup.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.my.fl.startup.entity.CabVerificationFeedbackEntity;
import com.my.fl.startup.entity.MembershipPlan;

@Repository
public interface MembershipPlanRepo extends JpaRepository<MembershipPlan, Long> {

	Optional<MembershipPlan> findByName(String string);

}
