package com.globo.assinaturas.Assinaturas.domain.service;

import com.globo.assinaturas.Assinaturas.domain.model.Assinatura;
import com.globo.assinaturas.Assinaturas.domain.model.Plano;
import com.globo.assinaturas.Assinaturas.domain.model.StatusAssinatura;
import com.globo.assinaturas.Assinaturas.domain.model.Usuario;
import com.globo.assinaturas.Assinaturas.domain.repository.AssinaturaRepository;
import com.globo.assinaturas.Assinaturas.domain.repository.UsuarioRepository;
import com.globo.assinaturas.Assinaturas.infrastructure.messaging.RabbitConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AssinaturaServiceTest {

	@Mock
	private AssinaturaRepository assinaturaRepository;

	@Mock
	private UsuarioRepository usuarioRepository;

	@Mock
	private RabbitTemplate rabbitTemplate;

	@InjectMocks
	private AssinaturaService assinaturaService;

	private UUID usuarioId;
	private Usuario usuario;
	private Assinatura assinatura;

	@BeforeEach
	void setUp() {
		usuarioId = UUID.randomUUID();
		usuario = new Usuario();
		usuario.setId(usuarioId);
		assinatura = new Assinatura();
		assinatura.setId(UUID.randomUUID());
		assinatura.setUsuario(usuario);
		assinatura.setPlano(Plano.BASICO);
		assinatura.setDataInicio(LocalDate.now());
		assinatura.setDataExpiracao(LocalDate.now().plusMonths(1));
		assinatura.setStatus(StatusAssinatura.ATIVA);
		assinatura.setTentativasPagamento(0);
	}

	@Test
	void criarAssinatura_usuarioNaoEncontrado() {
		when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> assinaturaService.criarAssinatura(usuarioId, Plano.BASICO));
	}

	@Test
	void criarAssinatura_usuarioJaPossuiAssinaturaAtiva() {
		when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
		when(assinaturaRepository.findByUsuarioAndStatus(usuario, StatusAssinatura.ATIVA))
				.thenReturn(Optional.of(assinatura));

		assertThrows(RuntimeException.class, () -> assinaturaService.criarAssinatura(usuarioId, Plano.BASICO));
	}

	@Test
	void criarAssinatura_assinaturaCriadaComSucesso() {
		when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
		when(assinaturaRepository.findByUsuarioAndStatus(usuario, StatusAssinatura.ATIVA)).thenReturn(Optional.empty());
		when(assinaturaRepository.save(any(Assinatura.class))).thenReturn(assinatura);

		Assinatura resultado = assinaturaService.criarAssinatura(usuarioId, Plano.BASICO);

		assertNotNull(resultado);
		assertEquals(assinatura, resultado);
	}

	@Test
	void obterAssinaturaAtiva_assinaturaEncontrada() {
		when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
		when(assinaturaRepository.findByUsuarioAndStatus(usuario, StatusAssinatura.ATIVA))
				.thenReturn(Optional.of(assinatura));

		Optional<Assinatura> resultado = assinaturaService.obterAssinaturaAtiva(usuarioId);

		assertTrue(resultado.isPresent());
		assertEquals(assinatura, resultado.get());
	}

	@Test
	void obterAssinaturaAtiva_assinaturaNaoEncontrada() {
		when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
		when(assinaturaRepository.findByUsuarioAndStatus(usuario, StatusAssinatura.ATIVA)).thenReturn(Optional.empty());

		Optional<Assinatura> resultado = assinaturaService.obterAssinaturaAtiva(usuarioId);

		assertFalse(resultado.isPresent());
	}

	@Test
	void renovarAssinaturas_renovacaoBemSucedida() {
		List<Assinatura> assinaturasExpirando = Arrays.asList(assinatura);
		when(assinaturaRepository.findByDataExpiracaoAndStatus(LocalDate.now(), StatusAssinatura.ATIVA))
				.thenReturn(assinaturasExpirando);

		// Simula sucesso no pagamento
		AssinaturaService assinaturaServiceSpy = spy(assinaturaService);
		doReturn(true).when(assinaturaServiceSpy).processarPagamento(any(Assinatura.class));

		assinaturaServiceSpy.renovarAssinaturas();

		assertEquals(0, assinatura.getTentativasPagamento());
		assertEquals(LocalDate.now().plusMonths(1), assinatura.getDataExpiracao()); // Verifica se a data de expiração foi atualizada
		assertEquals(StatusAssinatura.ATIVA, assinatura.getStatus()); // Verifica se o status permanece ativo
	}

	@Test
	void renovarAssinaturas_falhaNaRenovacao() {
		assinatura.setTentativasPagamento(2);
		List<Assinatura> assinaturasExpirando = Arrays.asList(assinatura);
		when(assinaturaRepository.findByDataExpiracaoAndStatus(LocalDate.now(), StatusAssinatura.ATIVA))
				.thenReturn(assinaturasExpirando);

		// Simula falha no pagamento
		AssinaturaService assinaturaServiceSpy = spy(assinaturaService);
		doReturn(false).when(assinaturaServiceSpy).processarPagamento(any(Assinatura.class));

		assinaturaServiceSpy.renovarAssinaturas();

		assertEquals(3, assinatura.getTentativasPagamento());

		verify(rabbitTemplate, times(1)).convertAndSend(eq(RabbitConfig.NOTIFICACAO_EXCHANGE),
				eq(RabbitConfig.ROUTING_KEY), anyString());
	}

	@Test
	void renovarAssinaturas_assinaturaSuspensa() {
		assinatura.setTentativasPagamento(3);
		List<Assinatura> assinaturasExpirando = Arrays.asList(assinatura);
		when(assinaturaRepository.findByDataExpiracaoAndStatus(LocalDate.now(), StatusAssinatura.ATIVA))
				.thenReturn(assinaturasExpirando);

		assinaturaService.renovarAssinaturas();

		assertEquals(StatusAssinatura.SUSPENSA, assinatura.getStatus());
		verify(rabbitTemplate, times(1)).convertAndSend(eq(RabbitConfig.NOTIFICACAO_EXCHANGE),
				eq(RabbitConfig.ROUTING_KEY), anyString());
	}

	@Test
	void cancelarAssinatura_usuarioNaoEncontrado() {
		when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> assinaturaService.cancelarAssinatura(usuarioId));
	}

	@Test
	void cancelarAssinatura_assinaturaNaoEncontrada() {
		when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
		when(assinaturaRepository.findByUsuarioAndStatus(usuario, StatusAssinatura.ATIVA)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> assinaturaService.cancelarAssinatura(usuarioId));
	}

	@Test
	void cancelarAssinatura_assinaturaCanceladaComSucesso() {
		when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
		when(assinaturaRepository.findByUsuarioAndStatus(usuario, StatusAssinatura.ATIVA))
				.thenReturn(Optional.of(assinatura));

		assinaturaService.cancelarAssinatura(usuarioId);

		assertEquals(StatusAssinatura.CANCELADA, assinatura.getStatus());
		verify(assinaturaRepository, times(1)).save(assinatura);
		verify(rabbitTemplate, times(1)).convertAndSend(eq(RabbitConfig.NOTIFICACAO_EXCHANGE),
				eq(RabbitConfig.ROUTING_KEY), anyString());
	}

	@Test
	void processarNotificacao_processaMensagem() {
		assinaturaService.processarNotificacao("Mensagem de teste");
		// Verifica se o método processarNotificacao foi chamado (pode ser necessário
		// adicionar mais verificações dependendo da implementação real)
	}
}
