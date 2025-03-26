package com.globo.assinaturas.Assinaturas.domain.service;

import com.globo.assinaturas.Assinaturas.domain.model.Usuario;
import com.globo.assinaturas.Assinaturas.domain.repository.UsuarioRepository;
import com.globo.assinaturas.Assinaturas.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private UUID usuarioId;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        usuario = new Usuario();
        usuario.setId(usuarioId);
    }

    @Test
    void criarUsuario_usuarioCriadoComSucesso() {
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        Usuario resultado = usuarioService.criarUsuario(usuario);

        assertEquals(usuario, resultado);
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void getUsuarioById_usuarioEncontrado() {
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.getUsuarioById(usuarioId);

        assertTrue(resultado.isPresent());
        assertEquals(usuario, resultado.get());
        verify(usuarioRepository, times(1)).findById(usuarioId);
    }

    @Test
    void getUsuarioById_usuarioNaoEncontrado_lancaExcecao() {
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.getUsuarioById(usuarioId));
        verify(usuarioRepository, times(1)).findById(usuarioId);
    }

    @Test
    void usuarioExiste_usuarioExiste() {
        when(usuarioRepository.existsById(usuarioId)).thenReturn(true);

        boolean resultado = usuarioService.usuarioExiste(usuarioId);

        assertTrue(resultado);
        verify(usuarioRepository, times(1)).existsById(usuarioId);
    }

    @Test
    void usuarioExiste_usuarioNaoExiste() {
        when(usuarioRepository.existsById(usuarioId)).thenReturn(false);

        boolean resultado = usuarioService.usuarioExiste(usuarioId);

        assertFalse(resultado);
        verify(usuarioRepository, times(1)).existsById(usuarioId);
    }
}
