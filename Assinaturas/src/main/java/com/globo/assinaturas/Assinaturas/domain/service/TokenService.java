package com.globo.assinaturas.Assinaturas.domain.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;

@Service
public class TokenService {
	
	private static final Logger logger = LoggerFactory.getLogger(TokenService.class);
	
	@Value("${api.security.token.secret}")
	private String secret;
	
	public String generateToken(UserDetails details) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(secret);
			String token = JWT.create()
					.withIssuer("Assinaturas")
					.withSubject(details.getUsername())
					.withExpiresAt(genExpirationDate())
					.sign(algorithm);
			
			return token;
					
		} catch (JWTCreationException e) {
			throw new RuntimeException("Error ao tentar gerar o token");
		}
		
	}
	
	public String validateToken(String token) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(secret);
			return JWT.require(algorithm)
					.withIssuer("Assinaturas")
					.build()
					.verify(token.trim())
					.getSubject();
					
		} catch (JWTCreationException | JWTDecodeException e) {
            logger.error("Token inválido: {}", e.getMessage());
            return null; 
		}
	}
	
	private Instant genExpirationDate() {
		return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
	}

}
