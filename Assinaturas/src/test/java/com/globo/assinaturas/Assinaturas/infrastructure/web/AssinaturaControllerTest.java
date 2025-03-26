package com.globo.assinaturas.Assinaturas.infrastructure.web;

import com.globo.assinaturas.Assinaturas.domain.model.Assinatura;
import com.globo.assinaturas.Assinaturas.domain.model.Plano;
import com.globo.assinaturas.Assinaturas.domain.model.StatusAssinatura;
import com.globo.assinaturas.Assinaturas.domain.model.Usuario;
import com.globo.assinaturas.Assinaturas.domain.service.AssinaturaService;
import com.globo.assinaturas.Assinaturas.domain.service.UsuarioService;
import com.globo.assinaturas.Assinaturas.dto.AssinaturaDTO;
import com.globo.assinaturas.Assinaturas.mapper.AssinaturaMapper;
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
public class AssinaturaControllerTest {

    @Mock
    private AssinaturaService assinaturaService;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private AssinaturaMapper assinaturaMapper;

    @InjectMocks
    private AssinaturaController assinaturaController;

    private UUID usuarioId;
    private Usuario usuario;
    private Assinatura assinatura;
    private AssinaturaDTO assinaturaDTO;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();

        usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNome("Aline");

        assinatura = new Assinatura();
        assinatura.setId(UUID.randomUUID());
        assinatura.setUsuario(usuario);
        assinatura.setPlano(Plano.PREMIUM);
        assinatura.setStatus(StatusAssinatura.ATIVA);
        
        assinaturaDTO = assinaturaMapper.toDTO(assinatura);
        
    }

    @Test
    void criarAssinatura_usuarioNaoEncontrado() {
        when(usuarioService.getUsuarioById(usuarioId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = assinaturaController.criarAssinatura(usuarioId, Plano.PREMIUM);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Usuário não encontrado.", response.getBody());
    }

    @Test
    void criarAssinatura_assinaturaCriadaComSucesso() {
        when(usuarioService.getUsuarioById(usuarioId)).thenReturn(Optional.of(usuario));
        when(assinaturaService.criarAssinatura(usuarioId, Plano.PREMIUM)).thenReturn(assinatura);
        when(assinaturaMapper.toDTO(assinatura)).thenReturn(assinaturaDTO);

        ResponseEntity<?> response = assinaturaController.criarAssinatura(usuarioId, Plano.PREMIUM);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(assinaturaDTO, response.getBody());
    }

    @Test
    void criarAssinatura_erroAoCriarAssinatura() {
        when(usuarioService.getUsuarioById(usuarioId)).thenReturn(Optional.of(usuario));
        when(assinaturaService.criarAssinatura(usuarioId, Plano.PREMIUM)).thenThrow(new RuntimeException("Erro ao criar assinatura."));

        ResponseEntity<?> response = assinaturaController.criarAssinatura(usuarioId, Plano.PREMIUM);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Erro ao criar assinatura.", response.getBody());
    }

    @Test
    void obterAssinaturaAtiva_assinaturaEncontrada() {
        when(assinaturaService.obterAssinaturaAtiva(usuarioId)).thenReturn(Optional.of(assinatura));
        when(assinaturaMapper.toDTO(assinatura)).thenReturn(assinaturaDTO);

        ResponseEntity<?> response = assinaturaController.obterAssinaturaAtiva(usuarioId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(assinaturaDTO, response.getBody());
    }

    @Test
    void cancelarAssinatura_sucesso() {
        ResponseEntity<?> response = assinaturaController.cancelarAssinatura(usuarioId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Assinatura cancelada com sucesso.", response.getBody());
        verify(assinaturaService, times(1)).cancelarAssinatura(usuarioId);
    }

    @Test
    void cancelarAssinatura_erro() {
        doThrow(new RuntimeException("Erro ao cancelar assinatura.")).when(assinaturaService).cancelarAssinatura(usuarioId);

        ResponseEntity<?> response = assinaturaController.cancelarAssinatura(usuarioId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Erro ao cancelar assinatura.", response.getBody());
    }
}
