package com.globo.assinaturas.Assinaturas.mapper;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.globo.assinaturas.Assinaturas.dto.AuthenticationDTO;

@Component
public class AuthenticationMapper {
	
	public AuthenticationDTO userDetailToDTO(UserDetails details) {
		if (details == null) {
			return null;
		}
		
		AuthenticationDTO dto = new AuthenticationDTO();
		dto.setEmail(details.getUsername());
		dto.setSenha(details.getPassword());
		
		return dto;
	}

}
