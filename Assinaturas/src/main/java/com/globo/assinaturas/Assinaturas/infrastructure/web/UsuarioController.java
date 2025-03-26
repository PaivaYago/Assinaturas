package com.globo.assinaturas.Assinaturas.infrastructure.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.globo.assinaturas.Assinaturas.config.SecurityConfig;
import com.globo.assinaturas.Assinaturas.domain.model.Usuario;
import com.globo.assinaturas.Assinaturas.domain.service.UsuarioService;
import com.globo.assinaturas.Assinaturas.dto.UsuarioDTO;
import com.globo.assinaturas.Assinaturas.mapper.UsuarioMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
@Tag(name="usuario", description = "Controlador de Usuario")
@SecurityRequirement(name = SecurityConfig.SECURITY)
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private UsuarioMapper usuarioMapper;

    @Operation(summary = "Cria um novo usuário.")
    @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso.", 
    			 content = @Content(schema = @Schema(implementation = UsuarioDTO.class)))
    @PostMapping
    public ResponseEntity<?> criarUsuario(@RequestBody UsuarioDTO usuarioDTO) {
    	
    	if(usuarioService.getUsuarioByEmail(usuarioDTO.getEmail()) != null) 
    		return ResponseEntity.badRequest().body("E-mail já cadastro para outro usuário");
    	
    	String encryptedPassword = new BCryptPasswordEncoder().encode(usuarioDTO.getSenha());
    	usuarioDTO.setSenha(encryptedPassword);
    	
        Usuario usuario = usuarioMapper.toEntity(usuarioDTO);   
        Usuario usuarioCriado = usuarioService.criarUsuario(usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioMapper.toDTO(usuarioCriado));

    }

    @Operation(summary = "Busca um usuário por ID.")
    @ApiResponse(responseCode = "200", description = "Usuário encontrado.", 
    			 content = @Content(schema = @Schema(implementation = UsuarioDTO.class)))
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarUsuarioPorId(
    		@Parameter(description = "ID do usuário a ser buscado.", required = true) @PathVariable UUID id) {
    	
    	Optional<Usuario> usuarioOptional = usuarioService.getUsuarioById(id);

        if (usuarioOptional.isPresent()) {
            return ResponseEntity.ok(usuarioMapper.toDTO(usuarioOptional.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } 	
    }
}

