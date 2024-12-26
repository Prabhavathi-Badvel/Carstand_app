package com.my.fl.startup.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.my.fl.startup.entity.CabRouteEntity;

@Repository
public interface CabRouteRepo extends JpaRepository<CabRouteEntity, Long> {

	@Query(value = "Select * From cab_route Where CAB_OWNER_ID =:cabOwnerId", nativeQuery = true)
	List<CabRouteEntity> findByOwnerId(@Param("cabOwnerId") String cabOwnerId);

	@Query(value = "Select * From cab_route Where CAB_OWNER_ID =:cabOwnerId and CAB_ID =:cabId", nativeQuery = true)
	List<CabRouteEntity> findByOwnerIdCabId(@Param("cabOwnerId") String cabOwnerId, @Param("cabId") String cabId);

	List<CabRouteEntity> findByCabId(String cabId);

	//List<CabRouteEntity> findByOwnerIdCabId(@Param("cabOwnerId") String cabOwnerId, @Param("cabId") String cabId);

	@Query("SELECT c FROM CabRouteEntity c WHERE "
			+ "(:sourceAddress IS NULL OR c.sourceAddress LIKE %:sourceAddress%) AND "
			+ "(:destination IS NULL OR c.destination LIKE %:destination%) AND "
			+ "(:pickUpDate IS NULL OR c.pickUpDate <= :pickUpDate) AND " + "c.status = :status")
	List<CabRouteEntity> findbySourceAddressAndDestinationAndPickUpDateAndStatus(
			@Param("sourceAddress") String sourceAddress, @Param("destination") String destination,
			@Param("pickUpDate") LocalDate pickUpDate, @Param("status") String status);

}
