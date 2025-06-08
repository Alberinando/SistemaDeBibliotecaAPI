package com.sistema;

import com.sistema.domain.entities.Livros;
import com.sistema.domain.repositories.LivrosRepository;
import com.sistema.domain.services.LivrosServices;
import com.sistema.infrastructure.exceptions.NotFoundException;
import com.sistema.web.dto.Livros.LivroCreateDTO;
import com.sistema.web.dto.Livros.LivroUpdateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LivrosServicesTest {

    @Mock
    private LivrosRepository livrosRepository;

    @InjectMocks
    private LivrosServices livrosServices;

    private Livros livroExemplo;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        livroExemplo = new Livros();
        livroExemplo.setId(1L);
        livroExemplo.setTitulo("Livro Exemplo");
        livroExemplo.setAutor("Autor Exemplo");
        livroExemplo.setCategoria("Categoria Exemplo");
        livroExemplo.setDisponibilidade(true);
        livroExemplo.setIsbn(1234567890123L);
        livroExemplo.setQuantidade(10);
    }

    @Test
    void deveBuscarTodosOsLivrosPaginados() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Livros> lista = List.of(livroExemplo);
        Page<Livros> pagina = new PageImpl<>(lista, pageable, lista.size());

        when(livrosRepository.findAll(pageable)).thenReturn(pagina);

        var resultado = livrosServices.findAll(pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals("Livro Exemplo", resultado.getContent().get(0).getTitulo());
    }

    @Test
    void deveBuscarLivroPorIdQuandoExistir() {
        when(livrosRepository.findById(1L)).thenReturn(Optional.of(livroExemplo));

        var dto = livrosServices.findById(1L);

        assertEquals("Livro Exemplo", dto.getTitulo());
    }

    @Test
    void deveLancarExcecaoQuandoLivroNaoExistir() {
        when(livrosRepository.findById(2L)).thenReturn(Optional.empty());

        NotFoundException excecao = assertThrows(NotFoundException.class, () -> {
            livrosServices.findById(2L);
        });

        assertEquals("Livro não encontrado", excecao.getMessage());
    }

    @Test
    void deveCriarLivroQuandoIsbnNaoExistir() {
        LivroCreateDTO dto = new LivroCreateDTO();
        dto.setTitulo("Novo Livro");
        dto.setAutor("Novo Autor");
        dto.setCategoria("Nova Categoria");
        dto.setDisponibilidade(true);
        dto.setIsbn(9876543210987L);
        dto.setQuantidade(5);

        when(livrosRepository.existsByIsbn(dto.getIsbn())).thenReturn(false);
        when(livrosRepository.save(any(Livros.class))).thenAnswer(invocation -> {
            Livros livroSalvo = invocation.getArgument(0);
            livroSalvo.setId(2L);
            return livroSalvo;
        });

        var resposta = livrosServices.create(dto);

        assertNotNull(resposta);
        assertEquals("Novo Livro", resposta.getTitulo());
        assertEquals(2L, resposta.getId());
    }

    @Test
    void deveLancarExcecaoAoCriarLivroComIsbnDuplicado() {
        LivroCreateDTO dto = new LivroCreateDTO();
        dto.setIsbn(1234567890123L); // mesmo ISBN do livroExemplo

        when(livrosRepository.existsByIsbn(dto.getIsbn())).thenReturn(true);

        Exception excecao = assertThrows(Exception.class, () -> {
            livrosServices.create(dto);
        });

        assertTrue(excecao.getMessage().contains("ISBN já cadastrado"));
    }

    @Test
    void deveAtualizarLivroQuandoExistir() {
        LivroUpdateDTO dto = new LivroUpdateDTO();
        dto.setId(1L);
        dto.setTitulo("Livro Atualizado");
        dto.setAutor("Autor Atualizado");
        dto.setCategoria("Categoria Atualizada");
        dto.setDisponibilidade(false);
        dto.setIsbn(1234567890123L);
        dto.setQuantidade(20);

        when(livrosRepository.findById(1L)).thenReturn(Optional.of(livroExemplo));
        when(livrosRepository.existsByIsbn(dto.getIsbn())).thenReturn(false);
        when(livrosRepository.save(any(Livros.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var dtoAtualizado = livrosServices.update(dto);

        assertEquals("Livro Atualizado", dtoAtualizado.getTitulo());
        assertEquals(20, dtoAtualizado.getQuantidade());
        assertFalse(dtoAtualizado.getDisponibilidade());
    }

    @Test
    void deveLancarExcecaoAoAtualizarLivroInexistente() {
        LivroUpdateDTO dto = new LivroUpdateDTO();
        dto.setId(99L);

        when(livrosRepository.findById(99L)).thenReturn(Optional.empty());

        NotFoundException excecao = assertThrows(NotFoundException.class, () -> {
            livrosServices.update(dto);
        });

        assertEquals("Livro não encontrado", excecao.getMessage());
    }

    @Test
    void deveDeletarLivroQuandoExistir() {
        when(livrosRepository.existsById(1L)).thenReturn(true);

        livrosServices.delete(1L);

        verify(livrosRepository, times(1)).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoDeletarLivroInexistente() {
        when(livrosRepository.existsById(2L)).thenReturn(false);

        NotFoundException excecao = assertThrows(NotFoundException.class, () -> {
            livrosServices.delete(2L);
        });

        assertEquals("Livro não encontrado", excecao.getMessage());
    }

}
