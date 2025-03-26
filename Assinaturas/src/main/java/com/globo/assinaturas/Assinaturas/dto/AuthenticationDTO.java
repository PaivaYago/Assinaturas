package com.globo.assinaturas.Assinaturas.dto;


import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;



public class AuthenticationDTO implements Serializable{
	
	private static final long serialVersionUID = -7336682959453662188L;
	
    @NotBlank
    private String email;

    @NotBlank
    private String senha;
    
    public AuthenticationDTO() {
		
	}

    public AuthenticationDTO(@NotBlank String email, @NotBlank String senha) {
		super();
		this.email = email;
		this.senha = senha;
	}



	public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
