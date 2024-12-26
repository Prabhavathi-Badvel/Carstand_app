package com.my.fl.startup.repo;

import com.my.fl.startup.entity.AdminStateDistrictEntity;
import com.my.fl.startup.entity.CabMembershipRequestDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminStateDistrictRepo extends JpaRepository<AdminStateDistrictEntity, String>   {

}
