package com.globo.assinaturas.Assinaturas.infrastructure.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.globo.assinaturas.Assinaturas.domain.model.Assinatura;
import com.globo.assinaturas.Assinaturas.domain.model.Plano;
import com.globo.assinaturas.Assinaturas.domain.model.Usuario;
import com.globo.assinaturas.Assinaturas.domain.service.AssinaturaService;
import com.globo.assinaturas.Assinaturas.domain.service.UsuarioService;
import com.globo.assinaturas.Assinaturas.dto.AssinaturaDTO;
import com.globo.assinaturas.Assinaturas.mapper.AssinaturaMapper;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/assinaturas")
public class AssinaturaController {

	@Autowired
	private AssinaturaService assinaturaService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private AssinaturaMapper assinaturaMapper;

	@PostMapping("/{usuarioId}")
	public ResponseEntity<?> criarAssinatura(@PathVariable UUID usuarioId, @RequestParam Plano plano) {
		Optional<Usuario> usuarioOpt = usuarioService.getUsuarioById(usuarioId);
		if (usuarioOpt.isEmpty()) {
			return ResponseEntity.badRequest().body("Usuário não encontrado.");
		}

		try {
			Assinatura assinatura = assinaturaService.criarAssinatura(usuarioOpt.get().getId(), plano);

			return ResponseEntity.status(HttpStatus.CREATED).body(assinaturaMapper.toDTO(assinatura));

		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/{usuarioId}")
	public ResponseEntity<?> obterAssinaturaAtiva(@PathVariable UUID usuarioId) {

		return ResponseEntity.ok(assinaturaMapper.toDTO(assinaturaService.obterAssinaturaAtiva(usuarioId).get()));

	}

	@DeleteMapping("/{usuarioId}")
	public ResponseEntity<?> cancelarAssinatura(@PathVariable UUID usuarioId) {
		try {
			assinaturaService.cancelarAssinatura(usuarioId);
			return ResponseEntity.ok("Assinatura cancelada com sucesso.");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}
