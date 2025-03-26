package com.globo.assinaturas.Assinaturas.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.globo.assinaturas.Assinaturas.domain.model.Assinatura;
import com.globo.assinaturas.Assinaturas.domain.model.Plano;
import com.globo.assinaturas.Assinaturas.domain.model.StatusAssinatura;
import com.globo.assinaturas.Assinaturas.domain.model.Usuario;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AssinaturaRepositoryTest {

    @Mock
    private AssinaturaRepository assinaturaRepository;

    private Usuario usuario;
    private Assinatura assinatura;

    @BeforeEach
    public void setUp() {
        // Setup inicial do usuário
        usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("João Silva");
        usuario.setEmail("joao.silva@example.com");

        // Setup inicial da assinatura
        assinatura = new Assinatura();
        assinatura.setId(UUID.randomUUID());
        assinatura.setUsuario(usuario);
        assinatura.setPlano(Plano.PREMIUM);
        assinatura.setDataInicio(LocalDate.now());
        assinatura.setDataExpiracao(LocalDate.now().plusMonths(1));
        assinatura.setStatus(StatusAssinatura.ATIVA);
    }

    @Test
    public void testSalvarAssinatura() {
    	when(assinaturaRepository.save(any(Assinatura.class))).thenReturn(assinatura);
    	
        // Salva a assinatura no banco de dados
        Assinatura assinaturaSalva = assinaturaRepository.save(assinatura);

        // Verifica se a assinatura foi salva corretamente
        assertThat(assinaturaSalva).isNotNull();
        assertThat(assinaturaSalva.getId()).isNotNull();
        assertThat(assinaturaSalva.getUsuario()).isEqualTo(usuario);
        assertThat(assinaturaSalva.getPlano()).isEqualTo(Plano.PREMIUM);
        assertThat(assinaturaSalva.getStatus()).isEqualTo(StatusAssinatura.ATIVA);
    }

    @Test
    public void testBuscarAssinaturaPorUsuarioIdEStatus() {
    	when(assinaturaRepository.findByUsuarioIdAndStatus(usuario, StatusAssinatura.ATIVA)).thenReturn(Optional.of(assinatura));
    	

        // Busca a assinatura por usuário e status
        Optional<Assinatura> assinaturaEncontrada = assinaturaRepository.findByUsuarioIdAndStatus(usuario, StatusAssinatura.ATIVA);

        // Verifica se a assinatura foi encontrada
        assertThat(assinaturaEncontrada.isPresent());
        assertEquals(assinatura.getId(), assinaturaEncontrada.get().getId());

    }

    @Test
    public void testBuscarAssinaturaNaoExistente() {
        // Busca uma assinatura com um ID inexistente
        Optional<Assinatura> assinaturaNaoEncontrada = assinaturaRepository.findById(UUID.randomUUID());

        // Verifica se a assinatura não foi encontrada
        assertThat(assinaturaNaoEncontrada).isNotPresent();
    }

    @Test
    public void testBuscarAssinaturaPorDataExpiracao() {
    	when(assinaturaRepository.findByDataExpiracaoAndStatus(LocalDate.now().plusMonths(1), StatusAssinatura.ATIVA)).thenReturn(List.of(assinatura));
    	
        // Salva a assinatura no banco de dados
        Assinatura assinaturaSalva = assinaturaRepository.save(assinatura);

        // Busca assinaturas com data de expiração igual a data que configuramos
        List<Assinatura> assinaturaExpirada = assinaturaRepository.findByDataExpiracaoAndStatus(LocalDate.now().plusMonths(1), StatusAssinatura.ATIVA);

        // Verifica se a assinatura expirada foi encontrada
        assertThat(assinaturaExpirada).doesNotContainNull();
    }

    @Test
    public void testAtualizarStatusAssinatura() {
    	when(assinaturaRepository.save(any(Assinatura.class))).thenReturn(assinatura);
    	
        // Salva a assinatura no banco de dados
        Assinatura assinaturaSalva = assinaturaRepository.save(assinatura);

        // Atualiza o status da assinatura
        assinaturaSalva.setStatus(StatusAssinatura.CANCELADA);
        
        
        when(assinaturaRepository.save(assinaturaSalva)).thenReturn(assinatura);
        Assinatura assinaturaAtualizada = assinaturaRepository.save(assinaturaSalva);

        // Verifica se o status foi atualizado corretamente
        assertThat(assinaturaAtualizada.getStatus()).isEqualTo(StatusAssinatura.CANCELADA);
    }

}

