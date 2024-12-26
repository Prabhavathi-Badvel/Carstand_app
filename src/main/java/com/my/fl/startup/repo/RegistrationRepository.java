package com.my.fl.startup.repo;

import com.my.fl.startup.entity.RegistrationEntity;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.my.fl.startup.entity.RegistrationEntity;

@Repository
public interface RegistrationRepository extends JpaRepository<RegistrationEntity, Long> {

//	@Query(name = "SELECT *FROM users", nativeQuery = true)
//	boolean existsByEmail(String email);

	@Query(name = "SELECT *FROM users where firstName = :firstName")
	RegistrationEntity findByFirstName(@Param(value = "firstName") String firstName);

//
//	@Query(name = "SELECT *FROM users where id = :id")
//	Optional<User> findById(@Param(value = "id") UUID id);
//
	@Query(name = "SELECT *FROM users where email = :email")
	RegistrationEntity findByEmail(@Param(value = "email") String email);

	@Query(name = "SELECT *FROM users where mobileNumber = :mobileNumber")
	RegistrationEntity findByMobileNumber(@Param(value = "mobileNumber") String mobileNumber);
	
	Optional<RegistrationEntity> findByCandidateID( String candidateID);

//	boolean existsByFirstName(String firstName);

	boolean existsByEmail(String email);

	RegistrationEntity findByEmailAndUserType(String username, String string);

	@Query("SELECT r FROM RegistrationEntity r WHERE (r.email = :username OR r.mobileNumber = :username) AND r.userType != :userType")
	RegistrationEntity findByEmailORMobileNumberAndUserTypeNot(String username, String userType);

	@Query("SELECT r FROM RegistrationEntity r WHERE (r.email = :username OR r.mobileNumber = :username)")
	RegistrationEntity findByEmailORMobileNumber(String username);

	@Transactional
	@Modifying
	@Query("UPDATE RegistrationEntity r SET r.status = :status WHERE r.email = :email")
	int updateStatusByEmail(@Param("status") String status, @Param("email") String email);

	@Transactional
	@Modifying
	@Query("UPDATE RegistrationEntity r SET r.status = :status WHERE r.mobileNumber = :mobileNumber")
	int updateStatusByMobileNumber(@Param("status") String status, @Param("mobileNumber") String mobileNumber);

	RegistrationEntity findByEmailOrMobileNumber(String travellerEmail, String mobileNumber);

	boolean existsByMobileNumber(String mobileNumber);

	@Query("SELECT r FROM RegistrationEntity r WHERE " +
		       "(r.regDate >= :fromRegDate AND r.regDate <= :toRegDate) " +
		       "AND (:mobileNumber IS NULL OR r.mobileNumber = :mobileNumber) " +
		       "AND (:email IS NULL OR r.email = :email)")
	List<RegistrationEntity> findAllData(LocalDateTime fromRegDate, LocalDateTime toRegDate, String mobileNumber,
			String email);

}
