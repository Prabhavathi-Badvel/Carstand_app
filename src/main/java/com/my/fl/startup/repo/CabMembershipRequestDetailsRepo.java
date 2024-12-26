package com.my.fl.startup.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.my.fl.startup.entity.CabMembershipRequestDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface CabMembershipRequestDetailsRepo extends JpaRepository<CabMembershipRequestDetails, Integer>   {

}
