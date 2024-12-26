package com.my.fl.startup.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.my.fl.startup.entity.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

	Token findByToken(String authToken);

	void deleteByToken(String tokenId);

	Integer deleteByTokenAndUsername(String tokenId, String username);
}
