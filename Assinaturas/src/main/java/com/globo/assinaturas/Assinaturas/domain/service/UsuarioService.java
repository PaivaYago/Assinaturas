package com.globo.assinaturas.Assinaturas.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.globo.assinaturas.Assinaturas.domain.model.Usuario;
import com.globo.assinaturas.Assinaturas.domain.repository.UsuarioRepository;
import com.globo.assinaturas.Assinaturas.exception.ResourceNotFoundException;

import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario criarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

       public Optional<Usuario> getUsuarioById(UUID usuarioId) {
        // Buscando o usuário no repositório
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(usuarioId);

        // Se não encontrar o usuário, lança a exceção personalizada
        if (!usuarioOptional.isPresent()) {
            throw new ResourceNotFoundException("Usuário", usuarioId);
        }

        return usuarioOptional;
    }


    public boolean usuarioExiste(UUID id) {
        return usuarioRepository.existsById(id);
    }
}

