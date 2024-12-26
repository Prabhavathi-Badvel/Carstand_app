package com.my.fl.startup.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.my.fl.startup.entity.UserRoleEntity;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {

	UserRoleEntity findByRegId(Long id);

	@Query("SELECT u FROM UserRoleEntity u WHERE u.roleId IN :ids AND u.regId = :id")
	List<UserRoleEntity> findByRoleIdAndRegId(@Param("ids") List<Long> ids, @Param("id") Long id);

}
