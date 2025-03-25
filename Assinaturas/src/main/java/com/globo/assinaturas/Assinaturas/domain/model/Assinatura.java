package com.globo.assinaturas.Assinaturas.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "assinaturas")
public class Assinatura implements Serializable{

	private static final long serialVersionUID = -3482370247299888419L;

	@Id
    @GeneratedValue
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Plano plano;
    
    @Column(nullable = false)
    private LocalDate dataInicio;
    
    @Column(nullable = false)
    private LocalDate dataExpiracao;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAssinatura status;
    
    @Column(nullable = false)
    private int tentativasPagamento;

}

