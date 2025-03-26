package com.globo.assinaturas.Assinaturas.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.globo.assinaturas.Assinaturas.domain.model.Usuario;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UsuarioRepositoryTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;

    @BeforeEach
    public void setUp() {
        // Setup inicial do usuário
        usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("Aline Martins");
        usuario.setEmail("aline.martins@example.com");


    }

    @Test
    public void testSalvarUsuario() {
    	when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
    	
        // Salva o usuário no banco de dados
        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        // Verifica se o usuário foi salvo corretamente
        assertThat(usuarioSalvo).isNotNull();
        assertThat(usuarioSalvo.getId()).isNotNull();
        assertThat(usuarioSalvo.getNome()).isEqualTo("Aline Martins");
        assertThat(usuarioSalvo.getEmail()).isEqualTo("aline.martins@example.com");

    }

    @Test
    public void testBuscarUsuarioPorId() {
    	when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));

        // Busca o usuário pelo ID
        Optional<Usuario> usuarioEncontrado = usuarioRepository.findById(usuario.getId());

        // Verifica se o usuário foi encontrado
        assertThat(usuarioEncontrado).isPresent();
        assertThat(usuarioEncontrado.get().getId()).isEqualTo(usuario.getId());
    }


    @Test
    public void testBuscarUsuarioNaoExistente() {
        // Busca um usuário com um ID inexistente
        Optional<Usuario> usuarioNaoEncontrado = usuarioRepository.findById(UUID.randomUUID());

        // Verifica se o usuário não foi encontrado
        assertThat(usuarioNaoEncontrado).isNotPresent();
    }


}

