package com.globo.assinaturas.Assinaturas.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.globo.assinaturas.Assinaturas.domain.model.Usuario;
import com.globo.assinaturas.Assinaturas.domain.repository.UsuarioRepository;
import com.globo.assinaturas.Assinaturas.domain.service.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter{
	
	@Autowired
	TokenService service;
	
	@Autowired
	UsuarioRepository repository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		var token = recoverToken(request);
		
		if(token != null) {
			var login = service.validateToken(token);
			Usuario usuario = repository.findByEmail(login); 
			
			UserDetails user = User.withUsername(usuario.getEmail())
	                .password(usuario.getSenha())
	                .roles("USER")
	                .build();
			
			var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
		}
		filterChain.doFilter(request, response);
		
	}
	
	private String recoverToken(HttpServletRequest httpServletRequest) {
		var authHeader = httpServletRequest.getHeader("Authorization");
		if(authHeader == null) return null;
		return authHeader.replace("Bearer", "");
	}
	

}
