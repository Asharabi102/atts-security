package com.asharabi.atts.security.jwt;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtUtil {

	// private String secret = "veryNotReallySecret";

	private SecretKey secret = Keys.secretKeyFor(SignatureAlgorithm.HS512);

	private final Logger log = LoggerFactory.getLogger(JwtUtil.class);

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
		final Claims claims = extractAllClaims(token);
		return claimResolver.apply(claims);
	}

	public Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public String generateToken(UserDetails userDetails) {
		return createToken(userDetails);
	}

	private String createToken(UserDetails userDetails) {
		return Jwts.builder().setSubject(userDetails.getUsername()).claim("authorities", userDetails.getAuthorities())
				.setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + 60000 * 60))
				.signWith(secret).compact();
	}

	private String createToken(Map<String, Object> claims, String subject) {
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 60000 * 60)).signWith(secret).compact();
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		Claims extractAllClaims = extractAllClaims(token); // it will throw exception if any claim does not match
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

}
