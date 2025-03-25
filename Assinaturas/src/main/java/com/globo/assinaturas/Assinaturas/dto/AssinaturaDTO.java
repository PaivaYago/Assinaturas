package com.globo.assinaturas.Assinaturas.dto;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssinaturaDTO {

    private UUID id;
    private UUID usuarioId;
    private String plano;
    private LocalDate dataInicio;
    private LocalDate dataExpiracao;
    private String status;

}

