package com.my.fl.startup.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.my.fl.startup.entity.MembershipRequest;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRequestRepo extends JpaRepository<MembershipRequest, Long>  {

}
