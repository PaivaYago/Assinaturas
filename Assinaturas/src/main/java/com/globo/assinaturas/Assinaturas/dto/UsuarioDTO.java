package com.globo.assinaturas.Assinaturas.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioDTO {

    private UUID id;
    private String nome;
    private String email;
    private String senha;

}

