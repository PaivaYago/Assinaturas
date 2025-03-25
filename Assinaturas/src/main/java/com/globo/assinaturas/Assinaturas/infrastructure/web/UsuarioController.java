package com.globo.assinaturas.Assinaturas.infrastructure.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.globo.assinaturas.Assinaturas.domain.model.Usuario;
import com.globo.assinaturas.Assinaturas.domain.service.UsuarioService;
import com.globo.assinaturas.Assinaturas.dto.UsuarioDTO;
import com.globo.assinaturas.Assinaturas.mapper.UsuarioMapper;

import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private UsuarioMapper usuarioMapper;

    @PostMapping
    public ResponseEntity<UsuarioDTO> criarUsuario(@RequestBody UsuarioDTO usuarioDTO) {
    	
        Usuario usuario = usuarioMapper.toEntity(usuarioDTO);   
        Usuario usuarioCriado = usuarioService.criarUsuario(usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioMapper.toDTO(usuarioCriado));

    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarUsuarioPorId(@PathVariable UUID id) {
    	
    	return ResponseEntity.ok(usuarioMapper.toDTO(usuarioService.getUsuarioById(id).get()));  	
    }
}

