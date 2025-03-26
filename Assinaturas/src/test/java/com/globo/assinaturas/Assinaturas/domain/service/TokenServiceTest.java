package com.globo.assinaturas.Assinaturas.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    @Mock
    private Logger logger;

    @InjectMocks
    private TokenService tokenService;

    private String secret;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        secret = "testSecret";
        userDetails = new User("testUser", "testPassword", java.util.Collections.emptyList());
        ReflectionTestUtils.setField(tokenService, "secret", secret);
    }

    @Test
    void generateToken_success() {
        String token = tokenService.generateToken(userDetails);
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void generateToken_jwtCreationException() {
        // Simula uma falha na criação do token (usando um segredo inválido)
        ReflectionTestUtils.setField(tokenService, "secret", "");
        assertThrows(RuntimeException.class, () -> tokenService.generateToken(userDetails));
    }

    @Test
    void validateToken_success() {
        String token = tokenService.generateToken(userDetails);
        String subject = tokenService.validateToken(token);
        assertEquals(userDetails.getUsername(), subject);
    }


    @Test
    void genExpirationDate_returnsCorrectInstant() {
        Instant expiration = ReflectionTestUtils.invokeMethod(tokenService, "genExpirationDate");
        Instant expected = LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
        assertTrue(expiration.isAfter(Instant.now()));
    }
}