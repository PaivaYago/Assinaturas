package com.globo.assinaturas.Assinaturas.infrastructure.web;

import com.globo.assinaturas.Assinaturas.domain.model.Usuario;
import com.globo.assinaturas.Assinaturas.domain.service.UsuarioService;
import com.globo.assinaturas.Assinaturas.dto.UsuarioDTO;
import com.globo.assinaturas.Assinaturas.mapper.UsuarioMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioController usuarioController;

    private UsuarioDTO usuarioDTO;
    private Usuario usuario;
    private UUID usuarioId;

    @BeforeEach
    void setUp() {

        usuario = new Usuario();
        usuarioId = UUID.randomUUID();
        usuario.setId(usuarioId);
        
        usuarioDTO = usuarioMapper.toDTO(usuario);
    }

    @Test
    void criarUsuario_usuarioCriadoComSucesso() {
        when(usuarioMapper.toEntity(usuarioDTO)).thenReturn(usuario);
        when(usuarioService.criarUsuario(usuario)).thenReturn(usuario);
        when(usuarioMapper.toDTO(usuario)).thenReturn(usuarioDTO);

        ResponseEntity<UsuarioDTO> response = usuarioController.criarUsuario(usuarioDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(usuarioDTO, response.getBody());
        verify(usuarioService, times(1)).criarUsuario(usuario);
    }

    @Test
    void buscarUsuarioPorId_usuarioEncontrado() {
        when(usuarioService.getUsuarioById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toDTO(usuario)).thenReturn(usuarioDTO);

        ResponseEntity<UsuarioDTO> response = usuarioController.buscarUsuarioPorId(usuarioId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(usuarioDTO, response.getBody());
        verify(usuarioService, times(1)).getUsuarioById(usuarioId);
    }
}

