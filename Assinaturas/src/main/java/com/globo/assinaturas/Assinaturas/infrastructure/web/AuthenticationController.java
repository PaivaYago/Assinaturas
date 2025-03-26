package com.globo.assinaturas.Assinaturas.infrastructure.web;

import javax.security.sasl.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.globo.assinaturas.Assinaturas.config.SecurityConfig;
import com.globo.assinaturas.Assinaturas.domain.service.TokenService;
import com.globo.assinaturas.Assinaturas.dto.AuthenticationDTO;
import com.globo.assinaturas.Assinaturas.dto.TokenResponseDTO;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;


@RestController
@RequestMapping("/login")
@SecurityRequirement(name = SecurityConfig.SECURITY)
public class AuthenticationController {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private TokenService tokenService;
	

    @Operation(summary = "Retorna do token.")
    @ApiResponse(responseCode = "201", 
    			 content = @Content(schema = @Schema(implementation = AuthenticationDTO.class)))
	@PostMapping
	public ResponseEntity<?> login(@RequestParam String email, @RequestParam String senha) throws AuthenticationException {
		
    	UsernamePasswordAuthenticationToken  usernamePassword = new UsernamePasswordAuthenticationToken(email, senha);
    	Authentication auth = this.authenticationManager.authenticate(usernamePassword);
		
    	UserDetails userDetails = (UserDetails) auth.getPrincipal();
    	String token = tokenService.generateToken(userDetails);
		return ResponseEntity.ok(new TokenResponseDTO(token));
		
	}
}
