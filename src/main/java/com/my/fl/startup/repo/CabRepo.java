package com.my.fl.startup.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.my.fl.startup.entity.AddCab;
import com.my.fl.startup.entity.enums.Status;

@Repository
public interface CabRepo extends JpaRepository<AddCab, Long> {

	@Query(value = "Select * From add_cab Where CAB_OWNER_ID =:userId", nativeQuery = true)
	List<AddCab> findByUserId(@Param("userId") String userId);

	@Query(value = "Select * From add_cab Where CAB_ADDED_DATE between :startDate and :endDate", nativeQuery = true)
	List<AddCab> findByDate(@Param("startDate") String startDate, @Param("endDate") String endDate);

	@Query(value = "Select * From add_cab Where CAB_OWNER_ID =:userId and CAB_ADDED_DATE between :startDate and :endDate", nativeQuery = true)
	List<AddCab> findByUserIdAndDate(@Param("userId") String userId, @Param("startDate") String startDate,
			@Param("endDate") String endDate);

	@Query(value = "Select * From add_cab Where CAB_REG_NO =:cabRegNo", nativeQuery = true)
	AddCab findBycabRegNo(@Param("cabRegNo") String cabRegNo);

	@Query(value = "Select * From add_cab Where CAB_REG_NO =:cabRegNo and CAB_OWNER_ID =:userId ", nativeQuery = true)
	AddCab findBycabRegNoAndCabOwnerId(@Param("cabRegNo") String cabRegNo, @Param("userId") String ownerId);

	@Query(value = "Select * From add_cab Where CAB_SEQ_ID =:cabSeqId and CAB_OWNER_ID =:userId ", nativeQuery = true)
	AddCab findByCabSeqIdAndCabOwnerId(@Param("cabSeqId") Long cabSeqId, @Param("userId") String ownerId);

	@Query(value = "Select * From add_cab Where CAB_GEN_ID =:cabGenId and CAB_OWNER_ID =:userId ", nativeQuery = true)
	AddCab findByCabGenIdAndCabOwnerId(@Param("cabGenId") String cabGenId, @Param("userId") String ownerId);

	@Query(value = "Select * From add_cab Where CAB_REG_NO =:cabRegId and CAB_OWNER_ID =:userId ", nativeQuery = true)
	AddCab findByCabRegNoAndCabOwnerId(@Param("cabRegId") String cabRegId, @Param("userId") String ownerId);

	List<AddCab> findByCabGenIdInAndCabModelAndStatus(List<String> availableCabGenIds, String vehicleType,
			String available);

	Page<AddCab> findByCabRegNoInAndCabModelAndStatus(List<String> availableCabGenIds, String vehicleType,
			Status active, Pageable pageable);

	Page<AddCab> findByCabRegNoIn(List<String> availableCabGenIds, Pageable pageable);

//	AddCab findByCabOwnerId(String cabOwnerId);
	Page<AddCab> findByCabRegNoInAndCabModelAndStatus(List<String> availableCabGenIds, String vehicleType,
			String status, Pageable pageable);

	AddCab findByCabRegNo(String cabId);

	AddCab findByCabGenId(String cabGenId);

	List<AddCab> findByCabBrand(String brand);

	@Query(value = "Select * From add_cab Where CAB_OWNER_ID =:userId ", nativeQuery = true)
	List<AddCab> findByCabOwnerId(@Param("userId") String ownerId);

	@Query(value = "Select * From add_cab Where CAB_GEN_ID =:cabGenid ", nativeQuery = true)
	List<AddCab> findAllByCabGenId(@Param("cabGenid") String cabGenid);

	@Query("SELECT c FROM AddCab c WHERE (:model IS NULL OR c.cabModel = :model) AND "
			+ "(:brand IS NULL OR c.cabBrand = :brand)AND (:cabOwnerId IS NULL OR c.cabOwnerId = :cabOwnerId) AND (:bodytype IS NULL OR c.bodyType = :bodytype) AND "
			+ "((:startDate IS NULL AND :endDate IS NULL) OR (c.cabAddedDate BETWEEN :startDate AND :endDate) OR "
			+ "(:startDate IS NULL AND c.cabAddedDate <= :endDate) OR "
			+ "(:endDate IS NULL AND c.cabAddedDate >= :startDate))")
	List<AddCab> findByCabModelAndCabBrandAndBodyTypeAndCabAddedDateAndCabOwnerId(String model, String brand,
			String bodytype, LocalDate startDate, LocalDate endDate, String cabOwnerId);

	@Query(value = "SELECT * FROM add_cab c " + "WHERE (:userId IS NULL OR c.CAB_OWNER_ID = :userId) "
			+ "AND ((:startDate IS NULL AND :endDate IS NULL) OR "
			+ "(c.CAB_ADDED_DATE BETWEEN :startDate AND :endDate)) "
			+ "AND (:cabRegNo IS NULL OR c.CAB_REG_NO = :cabRegNo)", nativeQuery = true)
	List<AddCab> findByUserIdAndCabAddedDate(@Param("userId") String userId, @Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate,@Param("cabRegNo") String cabRegNo);

	@Query("SELECT a FROM AddCab a WHERE a.cabOwnerId = :userId " + "AND (:brand IS NULL OR a.cabBrand = :brand) "
			+ "AND (:model IS NULL OR a.cabModel = :model) "
			+ "AND (:startDate IS NULL OR a.cabAddedDate >= :startDate) "
			+ "AND (:endDate IS NULL OR a.cabAddedDate <= :endDate)")
	List<AddCab> findByUserIdOrBrandOrModelOrStartDateBetweenAndEndDateBetween(@Param("userId") String userId,
			@Param("brand") String brand, @Param("model") String model, @Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);
}
