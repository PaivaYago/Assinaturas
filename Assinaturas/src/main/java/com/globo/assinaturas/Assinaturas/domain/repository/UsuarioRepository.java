package com.globo.assinaturas.Assinaturas.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import com.globo.assinaturas.Assinaturas.domain.model.Usuario;

import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
	
	Usuario findByEmail(String email);
}

