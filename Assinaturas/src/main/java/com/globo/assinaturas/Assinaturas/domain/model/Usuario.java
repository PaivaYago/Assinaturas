package com.globo.assinaturas.Assinaturas.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario implements Serializable{

	private static final long serialVersionUID = 4253542251299541402L;

	@Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(nullable = false)
    private String senha;
}

