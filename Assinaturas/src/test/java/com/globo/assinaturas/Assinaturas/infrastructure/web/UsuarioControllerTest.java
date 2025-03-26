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

        usuarioDTO = UsuarioDTO.builder()
                .email("test@example.com")
                .senha("senha123")
                .build();
    }

    @Test
    void criarUsuario_usuarioCriadoComSucesso() {
        when(usuarioService.getUsuarioByEmail(usuarioDTO.getEmail())).thenReturn(null);
        when(usuarioMapper.toEntity(usuarioDTO)).thenReturn(usuario);
        when(usuarioService.criarUsuario(usuario)).thenReturn(usuario);
        when(usuarioMapper.toDTO(usuario)).thenReturn(usuarioDTO);

        ResponseEntity<?> response = usuarioController.criarUsuario(usuarioDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(usuarioDTO.getEmail(), ((UsuarioDTO) response.getBody()).getEmail());
        assertEquals(usuarioDTO.getSenha(), ((UsuarioDTO) response.getBody()).getSenha());
        verify(usuarioService, times(1)).criarUsuario(usuario);
    }

    @Test
    void criarUsuario_emailJaCadastrado() {
        when(usuarioService.getUsuarioByEmail(usuarioDTO.getEmail())).thenReturn(usuario);

        ResponseEntity<?> response = usuarioController.criarUsuario(usuarioDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("E-mail já cadastro para outro usuário", response.getBody());
    }

    @Test
    void buscarUsuarioPorId_usuarioEncontrado() {
        when(usuarioService.getUsuarioById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toDTO(usuario)).thenReturn(usuarioDTO);

        ResponseEntity<UsuarioDTO> response = usuarioController.buscarUsuarioPorId(usuarioId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(usuarioDTO.getEmail(), response.getBody().getEmail());
        assertEquals(usuarioDTO.getSenha(), response.getBody().getSenha());
        verify(usuarioService, times(1)).getUsuarioById(usuarioId);
    }

    @Test
    void buscarUsuarioPorId_usuarioNaoEncontrado() {
        when(usuarioService.getUsuarioById(usuarioId)).thenReturn(Optional.empty());

        ResponseEntity<UsuarioDTO> response = usuarioController.buscarUsuarioPorId(usuarioId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}