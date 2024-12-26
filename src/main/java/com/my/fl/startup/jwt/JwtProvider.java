package com.my.fl.startup.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

import javax.crypto.SecretKey;

import io.jsonwebtoken.security.SignatureException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.my.fl.startup.entity.Token;
import com.my.fl.startup.repo.TokenRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class JwtProvider {

	@Value("${di.app.jwtSecret}")
	private String jwtSecret;

	@Value("${di.app.jwtExpiration}")
	private int jwtExpiration;

	@Autowired
	TokenRepository tokenRepository;

	public static String CURRENT_USER = "";

	public String generateJwtToken(String username, String ip) {
		SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
		String tokenId = UUID.randomUUID().toString();
		String jwt = Jwts.builder().setSubject(username).setIssuedAt(new Date()).setId(tokenId)
				.setExpiration(new Date((new Date()).getTime() + jwtExpiration * 1000)).signWith(key).compact();
		Token token = new Token();
		token.setUsername(username);
		token.setToken(tokenId);
		token.setIpAddress(ip);
		tokenRepository.save(token);
		return jwt;
	}


	public TokenInfo getTokenInfo(String token) {
		try {
			SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
			Claims claims = Jwts.parserBuilder()
					.setSigningKey(key)
					.build()
					.parseClaimsJws(token)
					.getBody();

			String username = claims.getSubject();
			String tokenId = claims.getId();

			return new TokenInfo(tokenId, username);
		} catch (SignatureException e) {
			throw new IllegalArgumentException("Invalid JWT token", e);
		}
	}

	public String generateJwtTokenUsingMobileNumber(String username, String ip) {
		SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
		String tokenId = UUID.randomUUID().toString();
		String jwt = Jwts.builder().setSubject(username).setIssuedAt(new Date()).setId(tokenId)
				.setExpiration(new Date((new Date()).getTime() + jwtExpiration * 1000)).signWith(key).compact();
		Token token = new Token();
		token.setUsername(username);
		token.setToken(tokenId);
		token.setIpAddress(ip);
		tokenRepository.save(token);
		return jwt;
	}

	public boolean validateJwtToken(String authToken) {
		try {
			SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
			String tokenId = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken).getBody()
					.getId();
			Token token = tokenRepository.findByToken(tokenId);
			if (token == null) {
				log.error("Expired JWT token -> Message: {}");
				return false;
			}
			return true;
		} catch (MalformedJwtException e) {
			log.error("Invalid JWT token -> Message: {}", e);
		} catch (ExpiredJwtException e) {
			log.error("Expired JWT token -> Message: {}", e);
		} catch (UnsupportedJwtException e) {
			log.error("Unsupported JWT token -> Message: {}", e);
		} catch (IllegalArgumentException e) {
			log.error("JWT claims string is empty -> Message: {}", e);
		}
		return false;
	}

	public String getUserNameFromJwtToken(String token) {
		SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
		String userName = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
		CURRENT_USER = userName;
		return userName;
	}

	public Claims getClaims(final String token) {
		try {
			SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
			Claims body = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
			return body;
		} catch (Exception e) {
			log.error(e.getMessage() + " => " + e);
		}
		return null;
	}

	public void deleteToken(String authToken) {
		try {
			authToken = authToken.replace("Bearer ", "");
			SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
			String tokenId = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken).getBody()
					.getId();
			Token token = tokenRepository.findByToken(tokenId);
			if (token != null) {
				tokenRepository.delete(token);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Expired JWT token -> Message: {}");
		}
	}

	public String generateToken(String username, String userType) {
		return Jwts.builder().setSubject(username).claim("userType", userType).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
				.signWith(SignatureAlgorithm.HS256, jwtSecret).compact();
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public String extractUserType(String token) {
		return extractClaim(token, claims -> claims.get("userType", String.class));
	}

	public boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(token).getBody();
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public boolean isTokenValid(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Transactional
	public Boolean deleteTokenForTraveller(String tokenId, String username) {
		return tokenRepository.deleteByTokenAndUsername(tokenId, username)>0;
	}
}