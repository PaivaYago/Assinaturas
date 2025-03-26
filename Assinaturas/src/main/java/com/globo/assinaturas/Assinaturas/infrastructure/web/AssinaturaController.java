package com.globo.assinaturas.Assinaturas.infrastructure.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.globo.assinaturas.Assinaturas.config.SecurityConfig;
import com.globo.assinaturas.Assinaturas.domain.model.Assinatura;
import com.globo.assinaturas.Assinaturas.domain.model.Plano;
import com.globo.assinaturas.Assinaturas.domain.model.Usuario;
import com.globo.assinaturas.Assinaturas.domain.service.AssinaturaService;
import com.globo.assinaturas.Assinaturas.domain.service.UsuarioService;
import com.globo.assinaturas.Assinaturas.dto.AssinaturaDTO;
import com.globo.assinaturas.Assinaturas.mapper.AssinaturaMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/assinaturas")
@SecurityRequirement(name = SecurityConfig.SECURITY)
public class AssinaturaController {

	@Autowired
	private AssinaturaService assinaturaService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private AssinaturaMapper assinaturaMapper;

	@Operation(summary = "Cria uma nova assinatura para um usuário.")
    @ApiResponse(responseCode = "201", description = "Assinatura criada com sucesso.",
    			 content = @Content(
    					   schema = @Schema(implementation = AssinaturaDTO.class)))
    @ApiResponse(responseCode = "400", description = "Usuário não encontrado ou erro ao criar assinatura.")
	@PostMapping("/{usuarioId}")
	public ResponseEntity<?> criarAssinatura(
			@Parameter(description = "ID do usuário para criar a assinatura.", required = true) @PathVariable UUID usuarioId,
            @Parameter(description = "Plano da assinatura.", required = true) @RequestParam Plano plano) {

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

	@Operation(summary = "Obtém a assinatura ativa de um usuário.")
    @ApiResponse(responseCode = "200", description = "Assinatura ativa encontrada.", 
    			 content = @Content(schema = @Schema(implementation = AssinaturaDTO.class)))
	@GetMapping("/{usuarioId}")
	public ResponseEntity<?> obterAssinaturaAtiva(
			@Parameter(description = "ID do usuário para obter a assinatura ativa.", required = true) @PathVariable UUID usuarioId) {

		return ResponseEntity.ok(assinaturaMapper.toDTO(assinaturaService.obterAssinaturaAtiva(usuarioId).get()));

	}

	@Operation(summary = "Cancela a assinatura de um usuário.")
    @ApiResponse(responseCode = "200", description = "Assinatura cancelada com sucesso.")
    @ApiResponse(responseCode = "400", description = "Erro ao cancelar assinatura.")
	@DeleteMapping("/{usuarioId}")
	public ResponseEntity<?> cancelarAssinatura(
			@Parameter(description = "ID do usuário para cancelar a assinatura.", required = true) @PathVariable UUID usuarioId) {
		try {
			assinaturaService.cancelarAssinatura(usuarioId);
			return ResponseEntity.ok("Assinatura cancelada com sucesso.");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}
