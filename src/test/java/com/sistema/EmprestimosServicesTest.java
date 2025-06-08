package com.sistema.domain.services;

import com.sistema.domain.entities.Emprestimos;
import com.sistema.domain.entities.Livros;
import com.sistema.domain.entities.Membros;
import com.sistema.domain.repositories.EmprestimosRepository;
import com.sistema.infrastructure.exceptions.NotFoundException;
import com.sistema.web.dto.Emprestimos.EmprestimoCreateDTO;
import com.sistema.web.dto.Emprestimos.EmprestimoResponseDTO;
import com.sistema.web.dto.Emprestimos.EmprestimoUpdateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmprestimosServicesTest {

    @InjectMocks
    private EmprestimosServices emprestimosServices;

    @Mock
    private EmprestimosRepository emprestimosRepository;

    @Mock
    private LivrosServices livrosServices;

    @Mock
    private MembrosServices membrosServices;

    @BeforeEach
    void inicializar() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveListarTodosOsEmprestimosPaginados() {
        Emprestimos emp = new Emprestimos();
        emp.setId(1L);
        emp.setLivro(new Livros());
        emp.setMembro(new Membros());
        emp.setDataEmprestimo(LocalDateTime.now());
        emp.setStatus(true);

        Page<Emprestimos> page = new PageImpl<>(List.of(emp));
        when(emprestimosRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<EmprestimoResponseDTO> resultado = emprestimosServices.findAll(PageRequest.of(0, 10));
        assertEquals(1, resultado.getTotalElements());
        assertTrue(resultado.getContent().get(0).getStatus());
    }

    @Test
    void deveBuscarEmprestimoPorId() {
        Emprestimos emp = new Emprestimos();
        emp.setId(1L);
        emp.setLivro(new Livros());
        emp.setMembro(new Membros());
        emp.setDataEmprestimo(LocalDateTime.now());
        emp.setStatus(true);

        when(emprestimosRepository.findById(1L)).thenReturn(Optional.of(emp));

        EmprestimoResponseDTO resultado = emprestimosServices.findById(1L);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void deveLancarExcecaoQuandoEmprestimoNaoEncontradoPorId() {
        when(emprestimosRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> emprestimosServices.findById(99L));
    }

    @Test
    void deveExcluirEmprestimoComSucesso() {
        when(emprestimosRepository.existsById(1L)).thenReturn(true);
        doNothing().when(emprestimosRepository).deleteById(1L);

        assertDoesNotThrow(() -> emprestimosServices.delete(1L));
        verify(emprestimosRepository).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoExcluirEmprestimoInexistente() {
        when(emprestimosRepository.existsById(1L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> emprestimosServices.delete(1L));
    }

    @Test
    void deveAtualizarEmprestimoComSucesso() {
        Emprestimos emp = new Emprestimos();
        emp.setId(1L);

        Livros livro = new Livros();
        Membros membro = new Membros();

        EmprestimoUpdateDTO dto = new EmprestimoUpdateDTO();
        dto.setId(1L);
        dto.setLivros(livro);
        dto.setMembros(membro);
        dto.setDataEmprestimo(LocalDateTime.now());
        dto.setDataDevolucao(LocalDateTime.now().plusDays(7));
        dto.setStatus(false);

        when(emprestimosRepository.findById(1L)).thenReturn(Optional.of(emp));
        when(emprestimosRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        EmprestimoResponseDTO resultado = emprestimosServices.update(dto);

        assertFalse(resultado.getStatus());
    }

    @Test
    void deveCriarEmprestimoComSucesso() {
        EmprestimoCreateDTO dto = new EmprestimoCreateDTO();
        dto.setLivroId(1L);
        dto.setMembroId(1L);
        dto.setDataEmprestimo(LocalDateTime.now());
        dto.setStatus(true);

        Livros livro = new Livros();
        Membros membro = new Membros();

        when(livrosServices.findEntityById(1L)).thenReturn(livro);
        when(membrosServices.findEntityById(1L)).thenReturn(membro);
        when(emprestimosRepository.save(any())).thenAnswer(invocation -> {
            Emprestimos e = invocation.getArgument(0);
            e.setId(1L);
            return e;
        });

        EmprestimoResponseDTO resultado = emprestimosServices.create(dto);
        assertTrue(resultado.getStatus());
        assertEquals(1L, resultado.getId());
    }
}
