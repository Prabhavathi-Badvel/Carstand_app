package com.my.fl.startup.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.my.fl.startup.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User>{

//	@Query(name = "SELECT *FROM users", nativeQuery = true)
//	boolean existsByEmail(String email);

	@Query(name = "SELECT *FROM users where userName = :userName")
	User findByUsername(@Param(value = "userName") String userName);
//
//	@Query(name = "SELECT *FROM users where id = :id")
//	Optional<User> findById(@Param(value = "id") UUID id);
//
	@Query(name = "SELECT *FROM users where email = :email")
	User findByEmail(@Param(value = "email") String email);

	@Query(name = "SELECT *FROM users where mobileNumber = :email")
	User findByMobileNumber(@Param(value = "mobileNumber") String mobileNumber);
	
	boolean existsByUsername(String username);
    boolean existsByEmail(String email);

}
