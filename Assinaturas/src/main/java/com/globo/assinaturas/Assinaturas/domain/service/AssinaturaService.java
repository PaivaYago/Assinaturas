package com.globo.assinaturas.Assinaturas.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.globo.assinaturas.Assinaturas.domain.model.Assinatura;
import com.globo.assinaturas.Assinaturas.domain.model.Plano;
import com.globo.assinaturas.Assinaturas.domain.model.StatusAssinatura;
import com.globo.assinaturas.Assinaturas.domain.model.Usuario;
import com.globo.assinaturas.Assinaturas.domain.repository.AssinaturaRepository;
import com.globo.assinaturas.Assinaturas.domain.repository.UsuarioRepository;
import com.globo.assinaturas.Assinaturas.infrastructure.messaging.RabbitConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@EnableCaching
public class AssinaturaService {
	private static final Logger logger = LoggerFactory.getLogger(AssinaturaService.class);

	@Autowired
	private AssinaturaRepository assinaturaRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	private static final int MAX_TENTATIVAS_PAGAMENTO = 3;

	@CacheEvict(value = "assinaturas", key = "#usuarioId")
	public Assinatura criarAssinatura(UUID usuarioId, Plano plano) {
		logger.info("Criando assinatura para usuário: {} com plano: {}", usuarioId, plano);

		Usuario usuario = usuarioRepository.findById(usuarioId)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

		Optional<Assinatura> assinaturaExistente = assinaturaRepository.findByUsuarioAndStatus(usuario,
				StatusAssinatura.ATIVA);
		if (assinaturaExistente.isPresent()) {
			throw new RuntimeException("Usuário já possui uma assinatura ativa");
		}

		Assinatura assinatura = new Assinatura();
		assinatura.setUsuario(usuario);
		assinatura.setPlano(plano);
		assinatura.setDataInicio(LocalDate.now());
		assinatura.setDataExpiracao(LocalDate.now().plusMonths(1));
		assinatura.setStatus(StatusAssinatura.ATIVA);
		assinatura.setTentativasPagamento(0);

		logger.info("Assinatura criada com sucesso para usuário: {}", usuarioId);
		return assinaturaRepository.save(assinatura);
	}

	@Cacheable(value = "assinaturas", key = "#usuarioId")
	public Optional<Assinatura> obterAssinaturaAtiva(UUID usuarioId) {
		return usuarioRepository.findById(usuarioId)
				.flatMap(usuario -> assinaturaRepository.findByUsuarioAndStatus(usuario, StatusAssinatura.ATIVA));
	}

	@Scheduled(cron = "* * * * * *") // Executa diariamente à meia-noite
	@CacheEvict(value = "assinaturas", allEntries = true)
	public void renovarAssinaturas() {
		logger.info("Iniciando processo de renovação de assinaturas");
		List<Assinatura> assinaturasExpirando = assinaturaRepository.findByDataExpiracaoAndStatus(LocalDate.now(),
				StatusAssinatura.ATIVA);

		for (Assinatura assinatura : assinaturasExpirando) {
			logger.info("Tentando renovar assinatura: {} para usuário: {}", assinatura.getId(),
					assinatura.getUsuario().getId());

			if (assinatura.getTentativasPagamento() < MAX_TENTATIVAS_PAGAMENTO) {
				boolean pagamentoBemSucedido = processarPagamento(assinatura);

				if (pagamentoBemSucedido) {
					assinatura.setDataInicio(LocalDate.now());
					assinatura.setDataExpiracao(LocalDate.now().plusMonths(1));
					assinatura.setTentativasPagamento(0);
					logger.info("Renovação bem-sucedida para assinatura: {}", assinatura.getId());
					rabbitTemplate.convertAndSend(RabbitConfig.NOTIFICACAO_EXCHANGE, RabbitConfig.ROUTING_KEY,
							"Assinatura renovada com sucesso para usuário: " + assinatura.getUsuario().getId());

				} else {
					assinatura.setTentativasPagamento(assinatura.getTentativasPagamento() + 1);
					logger.warn("Falha na renovação da assinatura: {} - Tentativa {}/{}", assinatura.getId(),
							assinatura.getTentativasPagamento(), MAX_TENTATIVAS_PAGAMENTO);
					rabbitTemplate.convertAndSend(RabbitConfig.NOTIFICACAO_EXCHANGE, RabbitConfig.ROUTING_KEY, "Falha na renovação da assinatura para usuário: "
							+ assinatura.getUsuario().getId() + " - Tentativa " + assinatura.getTentativasPagamento());
				}

			} else {
				
				assinatura.setStatus(StatusAssinatura.SUSPENSA);
				logger.error("Assinatura suspensa devido a múltiplas falhas de pagamento: {}",
						assinatura.getId());
				rabbitTemplate.convertAndSend(RabbitConfig.NOTIFICACAO_EXCHANGE, RabbitConfig.ROUTING_KEY,
						"Assinatura suspensa para usuário: " + assinatura.getUsuario().getId());
			}
			
			assinaturaRepository.save(assinatura);
		}
	}

	@CacheEvict(value = "assinaturas", key = "#usuarioId")
	public void cancelarAssinatura(UUID usuarioId) {
		logger.info("Cancelando assinatura para usuário: {}", usuarioId);

		Usuario usuario = usuarioRepository.findById(usuarioId)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

		Assinatura assinatura = assinaturaRepository.findByUsuarioAndStatus(usuario, StatusAssinatura.ATIVA)
				.orElseThrow(() -> new RuntimeException("Nenhuma assinatura ativa encontrada"));

		assinatura.setStatus(StatusAssinatura.CANCELADA);
		assinaturaRepository.save(assinatura);

		rabbitTemplate.convertAndSend(RabbitConfig.NOTIFICACAO_EXCHANGE, RabbitConfig.ROUTING_KEY, "Assinatura cancelada para usuário: " + usuarioId);
		logger.info("Assinatura cancelada com sucesso para usuário: {}", usuarioId);
	}

	boolean processarPagamento(Assinatura assinatura) {
		boolean sucesso = Math.random() > 0.2;
		logger.info("Processando pagamento para assinatura: {} - Sucesso: {}", assinatura.getId(), sucesso);
		return sucesso;
	}

	@RabbitListener(queues = RabbitConfig.NOTIFICACAO_QUEUE)
	public void processarNotificacao(String mensagem) {
		logger.info("Mensagem recebida da fila RabbitMQ: {}", mensagem);
		//TODO Criar uma funcionalidade para enviar notificação por e-mail.
	}
}
