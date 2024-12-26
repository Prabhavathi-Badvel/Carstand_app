package com.my.fl.startup.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.my.fl.startup.entity.Role;
import com.my.fl.startup.entity.RoleName;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
	Optional<Role> findByName(RoleName roleName);
	
}
