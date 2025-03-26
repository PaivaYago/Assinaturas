package com.globo.assinaturas.Assinaturas.infrastructure.web;

import com.globo.assinaturas.Assinaturas.domain.service.TokenService;
import com.globo.assinaturas.Assinaturas.dto.TokenResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.security.sasl.AuthenticationException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private String email;
    private String senha;
    private UserDetails userDetails;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        email = "test@example.com";
        senha = "password123";
        userDetails = new User(email, senha, Collections.emptyList());
        authentication = mock(Authentication.class);
    }

    @Test
    void login_authenticationSuccessful() throws AuthenticationException {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(tokenService.generateToken(userDetails)).thenReturn("testToken");

        ResponseEntity<?> response = authenticationController.login(email, senha);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testToken", ((TokenResponseDTO) response.getBody()).token());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService, times(1)).generateToken(userDetails);
    }

    @Test
    void login_authenticationFailed() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> authenticationController.login(email, senha));
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService, never()).generateToken(any(UserDetails.class));
    }

    @Test
    void login_authenticationException() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new RuntimeException("Test exception"));

        assertThrows(RuntimeException.class, () -> authenticationController.login(email, senha));
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService, never()).generateToken(any(UserDetails.class));
    }
}