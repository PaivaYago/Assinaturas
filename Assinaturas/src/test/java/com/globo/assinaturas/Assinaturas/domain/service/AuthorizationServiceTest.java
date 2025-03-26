package com.globo.assinaturas.Assinaturas.domain.service;

import com.globo.assinaturas.Assinaturas.domain.model.Usuario;
import com.globo.assinaturas.Assinaturas.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthorizationServiceTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private AuthorizationService authorizationService;

    private String email;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        email = "test@example.com";
        usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setSenha("password123");
    }

    @Test
    void loadUserByUsername_userFound() {
        when(repository.findByEmail(email)).thenReturn(usuario);

        UserDetails userDetails = authorizationService.loadUserByUsername(email);

        assertEquals(email, userDetails.getUsername());
        assertEquals(usuario.getSenha(), userDetails.getPassword());
        assertEquals("ROLE_USER", userDetails.getAuthorities().iterator().next().toString());
        verify(repository, times(1)).findByEmail(email);
    }

    @Test
    void loadUserByUsername_userNotFound() {
        when(repository.findByEmail(email)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> authorizationService.loadUserByUsername(email));
        verify(repository, times(1)).findByEmail(email);
    }
}