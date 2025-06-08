package com.sistema;

import com.sistema.domain.entities.Membros;
import com.sistema.domain.repositories.MembrosRepository;
import com.sistema.domain.services.MembrosServices;
import com.sistema.infrastructure.exceptions.NotFoundException;
import com.sistema.web.dto.Membros.MembrosCreateDTO;
import com.sistema.web.dto.Membros.MembrosUpdateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MembrosServicesTest {

    @Mock
    private MembrosRepository membrosRepository;

    @InjectMocks
    private MembrosServices membrosServices;

    private Membros membroExemplo;

    @BeforeEach
    void configurar() {
        MockitoAnnotations.openMocks(this);

        membroExemplo = new Membros();
        membroExemplo.setId(1L);
        membroExemplo.setNome("João Silva");
        membroExemplo.setCpf(12345678901L);
        membroExemplo.setTelefone(11999999999L);
        membroExemplo.setEmail("joao@email.com");
    }

    @Test
    void deveRetornarPaginaDeMembros() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Membros> lista = List.of(membroExemplo);
        Page<Membros> pagina = new PageImpl<>(lista, pageable, lista.size());

        when(membrosRepository.findAll(pageable)).thenReturn(pagina);

        var resultado = membrosServices.findAll(pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals("João Silva", resultado.getContent().get(0).getNome());
    }

    @Test
    void deveRetornarMembroPorIdQuandoExistir() {
        when(membrosRepository.findById(1L)).thenReturn(Optional.of(membroExemplo));

        var dto = membrosServices.findById(1L);

        assertEquals("João Silva", dto.getNome());
        assertEquals(12345678901L, dto.getCpf());
    }

    @Test
    void deveLancarExcecaoQuandoMembroNaoExistir() {
        when(membrosRepository.findById(2L)).thenReturn(Optional.empty());

        NotFoundException excecao = assertThrows(NotFoundException.class, () -> {
            membrosServices.findById(2L);
        });

        assertEquals("Membro não encontrado!", excecao.getMessage());
    }

    @Test
    void deveCriarMembroQuandoCpfEEmailNaoExistirem() {
        MembrosCreateDTO dto = new MembrosCreateDTO();
        dto.setNome("Maria Oliveira");
        dto.setCpf(98765432100L);
        dto.setTelefone(11888888888L);
        dto.setEmail("maria@email.com");

        when(membrosRepository.existsByCpf(dto.getCpf())).thenReturn(false);
        when(membrosRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(membrosRepository.save(any(Membros.class))).thenAnswer(invocation -> {
            Membros salvo = invocation.getArgument(0);
            salvo.setId(2L);
            return salvo;
        });

        var resposta = membrosServices.create(dto);

        assertNotNull(resposta);
        assertEquals("Maria Oliveira", resposta.getNome());
        assertEquals(2L, resposta.getId());
    }

    @Test
    void deveLancarExcecaoAoCriarMembroComCpfDuplicado() {
        MembrosCreateDTO dto = new MembrosCreateDTO();
        dto.setCpf(12345678901L);

        when(membrosRepository.existsByCpf(dto.getCpf())).thenReturn(true);

        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
            membrosServices.create(dto);
        });

        assertEquals("CPF já existe!", excecao.getMessage());
    }

    @Test
    void deveLancarExcecaoAoCriarMembroComEmailDuplicado() {
        MembrosCreateDTO dto = new MembrosCreateDTO();
        dto.setCpf(98765432100L);
        dto.setEmail("joao@email.com");

        when(membrosRepository.existsByCpf(dto.getCpf())).thenReturn(false);
        when(membrosRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
            membrosServices.create(dto);
        });

        assertEquals("Email já existe!", excecao.getMessage());
    }

    @Test
    void deveAtualizarMembroQuandoExistir() {
        MembrosUpdateDTO dto = new MembrosUpdateDTO();
        dto.setId(1L);
        dto.setNome("João Atualizado");
        dto.setCpf(12345678901L);
        dto.setTelefone(11911111111L);
        dto.setEmail("joao.atualizado@email.com");

        when(membrosRepository.findById(1L)).thenReturn(Optional.of(membroExemplo));
        when(membrosRepository.save(any(Membros.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var resposta = membrosServices.update(dto);

        assertEquals("João Atualizado", resposta.getNome());
        assertEquals(11911111111L, resposta.getTelefone());
        assertEquals("joao.atualizado@email.com", resposta.getEmail());
    }

    @Test
    void deveLancarExcecaoAoAtualizarMembroInexistente() {
        MembrosUpdateDTO dto = new MembrosUpdateDTO();
        dto.setId(99L);

        when(membrosRepository.findById(99L)).thenReturn(Optional.empty());

        NotFoundException excecao = assertThrows(NotFoundException.class, () -> {
            membrosServices.update(dto);
        });

        assertEquals("Membro não encontrado!", excecao.getMessage());
    }

    @Test
    void deveDeletarMembroQuandoExistir() {
        when(membrosRepository.existsById(1L)).thenReturn(true);

        membrosServices.delete(1L);

        verify(membrosRepository, times(1)).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoDeletarMembroInexistente() {
        when(membrosRepository.existsById(2L)).thenReturn(false);

        NotFoundException excecao = assertThrows(NotFoundException.class, () -> {
            membrosServices.delete(2L);
        });

        assertEquals("Membro não encontrado!", excecao.getMessage());
    }

    @Test
    void deveRetornarListaDeMembros() {
        List<Membros> lista = List.of(membroExemplo);

        when(membrosRepository.findAll()).thenReturn(lista);

        var resultado = membrosServices.findAllList();

        assertEquals(1, resultado.size());
        assertEquals("João Silva", resultado.get(0).getNome());
    }
}
