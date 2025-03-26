package com.globo.assinaturas.Assinaturas.mapper;

import org.springframework.stereotype.Component;

import com.globo.assinaturas.Assinaturas.domain.model.Usuario;
import com.globo.assinaturas.Assinaturas.dto.UsuarioDTO;

@Component
public class UsuarioMapper {

	public UsuarioDTO toDTO(Usuario usuario) {
		if (usuario == null) {
			return null;
		}

		return UsuarioDTO.builder().id(usuario.getId()).nome(usuario.getNome()).email(usuario.getEmail()).senha(usuario.getSenha()).build();
	}

	public Usuario toEntity(UsuarioDTO usuarioDTO) {
		if (usuarioDTO == null) {
			return null;
		}

		Usuario usuario = new Usuario();
		usuario.setId(usuarioDTO.getId());
		usuario.setNome(usuarioDTO.getNome());
		usuario.setEmail(usuarioDTO.getEmail());
		usuario.setSenha(usuarioDTO.getSenha());

		return usuario;
	}
	
}
