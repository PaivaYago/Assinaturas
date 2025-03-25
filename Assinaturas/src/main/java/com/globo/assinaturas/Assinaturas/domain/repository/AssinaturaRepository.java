package com.globo.assinaturas.Assinaturas.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.globo.assinaturas.Assinaturas.domain.model.Assinatura;
import com.globo.assinaturas.Assinaturas.domain.model.StatusAssinatura;
import com.globo.assinaturas.Assinaturas.domain.model.Usuario;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssinaturaRepository extends JpaRepository<Assinatura, UUID> {
	
    Optional<Assinatura> findByUsuarioIdAndStatus(Usuario usuario, StatusAssinatura status);
    
    Optional<Assinatura> findByUsuarioAndStatus(Usuario usuario, StatusAssinatura status);

    List<Assinatura> findByDataExpiracaoAndStatus(LocalDate dataExpiracao, StatusAssinatura status);
    
}

