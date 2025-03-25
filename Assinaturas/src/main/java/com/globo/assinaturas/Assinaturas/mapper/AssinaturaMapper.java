package com.globo.assinaturas.Assinaturas.mapper;

import org.springframework.stereotype.Component;

import com.globo.assinaturas.Assinaturas.domain.model.Assinatura;
import com.globo.assinaturas.Assinaturas.domain.model.Plano;
import com.globo.assinaturas.Assinaturas.domain.model.StatusAssinatura;
import com.globo.assinaturas.Assinaturas.dto.AssinaturaDTO;

@Component
public class AssinaturaMapper {

    public AssinaturaDTO toDTO(Assinatura assinatura) {
        return AssinaturaDTO.builder()
                .id(assinatura.getId())
                .usuarioId(assinatura.getUsuario().getId())
                .plano(assinatura.getPlano().name())
                .dataInicio(assinatura.getDataInicio())
                .dataExpiracao(assinatura.getDataExpiracao())
                .status(assinatura.getStatus().name())
                .build();
    }

    public Assinatura toEntity(AssinaturaDTO assinaturaDTO) {
        Assinatura assinatura = new Assinatura();
        assinatura.setId(assinaturaDTO.getId());
        assinatura.setPlano(Plano.valueOf(assinaturaDTO.getPlano()));
        assinatura.setDataInicio(assinaturaDTO.getDataInicio());
        assinatura.setDataExpiracao(assinaturaDTO.getDataExpiracao());
        assinatura.setStatus(StatusAssinatura.valueOf(assinaturaDTO.getStatus()));
        return assinatura;
    }
}