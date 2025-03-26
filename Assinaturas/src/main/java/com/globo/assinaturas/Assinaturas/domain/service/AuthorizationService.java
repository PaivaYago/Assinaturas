package com.globo.assinaturas.Assinaturas.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.globo.assinaturas.Assinaturas.domain.model.Usuario;
import com.globo.assinaturas.Assinaturas.domain.repository.UsuarioRepository;

@Service
public class AuthorizationService implements UserDetailsService{

	@Autowired
	UsuarioRepository repository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
	       Usuario usuario = repository.findByEmail(email);

	        if (usuario == null) {
	            throw new UsernameNotFoundException("Usuário não encontrado com o email: " + email);
	        }

	        return User.withUsername(usuario.getEmail())
	                .password(usuario.getSenha())
	                .roles("USER")
	                .build();
	}

}
