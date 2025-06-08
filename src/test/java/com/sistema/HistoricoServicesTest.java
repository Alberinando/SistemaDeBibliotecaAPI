package com.sistema;

import com.sistema.domain.entities.Historico;
import com.sistema.domain.entities.Livros;
import com.sistema.domain.entities.Membros;
import com.sistema.domain.repositories.HistoricoRepository;
import com.sistema.domain.services.HistoricoServices;
import com.sistema.infrastructure.exceptions.NotFoundException;
import com.sistema.web.dto.Historico.HistoricoResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HistoricoServicesTest {

    private HistoricoRepository historicoRepository;
    private HistoricoServices historicoServices;

    @BeforeEach
    void configurar() {
        historicoRepository = Mockito.mock(HistoricoRepository.class);
        historicoServices = new HistoricoServices(historicoRepository);
    }

    @Test
    void deveRetornarPaginaDeHistoricoResponseDTO_quandoBuscarTodos() {
        Livros livro = new Livros();
        livro.setId(1L);
        Membros membro = new Membros();
        membro.setId(2L);

        Historico historico = new Historico(1L, livro, membro, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());

        Page<Historico> paginaHistorico = new PageImpl<>(List.of(historico));
        when(historicoRepository.findAll(ArgumentMatchers.any(PageRequest.class))).thenReturn(paginaHistorico);

        Page<HistoricoResponseDTO> resultado = historicoServices.findAll(PageRequest.of(0, 10));

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        HistoricoResponseDTO dto = resultado.getContent().get(0);
        assertEquals(historico.getId(), dto.getId());
        assertEquals(historico.getLivros(), dto.getLivros());
        assertEquals(historico.getMembros(), dto.getMembros());
        assertEquals(historico.getDataAcao(), dto.getDataAcao());

        verify(historicoRepository, times(1)).findAll(ArgumentMatchers.any(PageRequest.class));
    }

    @Test
    void deveRetornarHistoricoResponseDTO_quandoBuscarPorIdExistente() {
        Livros livro = new Livros();
        livro.setId(1L);
        Membros membro = new Membros();
        membro.setId(2L);
        Historico historico = new Historico(1L, livro, membro, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());

        when(historicoRepository.findById(1L)).thenReturn(Optional.of(historico));

        HistoricoResponseDTO dto = historicoServices.findById(1L);

        assertNotNull(dto);
        assertEquals(historico.getId(), dto.getId());
        assertEquals(historico.getLivros(), dto.getLivros());
        assertEquals(historico.getMembros(), dto.getMembros());
        assertEquals(historico.getDataAcao(), dto.getDataAcao());

        verify(historicoRepository, times(1)).findById(1L);
    }

    @Test
    void deveLancarNotFoundException_quandoBuscarPorIdInexistente() {
        when(historicoRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException excecao = assertThrows(NotFoundException.class, () -> {
            historicoServices.findById(999L);
        });

        assertEquals("Histórico não encontrado", excecao.getMessage());

        verify(historicoRepository, times(1)).findById(999L);
    }
}
