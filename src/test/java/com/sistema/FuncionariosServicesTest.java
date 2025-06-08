package com.sistema;

import com.sistema.domain.entities.Funcionarios;
import com.sistema.domain.repositories.FuncionariosRepository;
import com.sistema.domain.services.FuncionariosServices;
import com.sistema.infrastructure.config.AccessToken;
import com.sistema.infrastructure.exceptions.DuplicatedTupleException;
import com.sistema.infrastructure.exceptions.NotFoundException;
import com.sistema.infrastructure.security.Jwt.Jwt;
import com.sistema.web.dto.Funcionarios.FuncionarioCreateDTO;
import com.sistema.web.dto.Funcionarios.FuncionarioResponseDTO;
import com.sistema.web.dto.Funcionarios.FuncionarioUpdateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FuncionariosServicesTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private FuncionariosRepository funcionariosRepository;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private FuncionariosServices funcionariosServices;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveRetornarTodosOsFuncionarios() {
        Funcionarios funcionario = new Funcionarios(1L, "João", "Analista", "joao", "senha", null, null);
        Page<Funcionarios> page = new PageImpl<>(List.of(funcionario));

        when(funcionariosRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<FuncionarioResponseDTO> result = funcionariosServices.findAll(PageRequest.of(0, 10));
        assertEquals(1, result.getTotalElements());
        assertEquals("João", result.getContent().get(0).getNome());
    }

    @Test
    void deveRetornarFuncionarioPorId() {
        Funcionarios funcionario = new Funcionarios(1L, "Maria", "Gerente", "maria", "senha", null, null);
        when(funcionariosRepository.findById(1L)).thenReturn(Optional.of(funcionario));

        FuncionarioResponseDTO dto = funcionariosServices.findById(1L);
        assertEquals("Maria", dto.getNome());
    }

    @Test
    void deveLancarExcecao_QuandoFuncionarioNaoEncontradoPorId() {
        when(funcionariosRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> funcionariosServices.findById(1L));
    }

    @Test
    void deveExcluirFuncionario() {
        when(funcionariosRepository.existsById(1L)).thenReturn(true);
        funcionariosServices.delete(1L);
        verify(funcionariosRepository).deleteById(1L);
    }

    @Test
    void deveLancarExcecao_AoExcluirFuncionarioInexistente() {
        when(funcionariosRepository.existsById(1L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> funcionariosServices.delete(1L));
    }

    @Test
    void deveAtualizarFuncionario() {
        Funcionarios funcionario = new Funcionarios(1L, "João", "Dev", "joao", "senha", null, null);
        when(funcionariosRepository.findById(1L)).thenReturn(Optional.of(funcionario));
        when(funcionariosRepository.save(any())).thenReturn(funcionario);

        FuncionarioUpdateDTO dto = new FuncionarioUpdateDTO(1L, "João Atualizado", "Dev Sênior", "joao");

        FuncionarioResponseDTO updated = funcionariosServices.update(dto);
        assertEquals("João Atualizado", updated.getNome());
    }

    @Test
    void deveSalvarNovoFuncionario() {
        FuncionarioCreateDTO dto = new FuncionarioCreateDTO("Carlos", "RH", "carlos", "123");

        when(funcionariosRepository.findByLogin("carlos")).thenReturn(null);
        when(passwordEncoder.encode("carlos")).thenReturn("hash");
        when(funcionariosRepository.save(any())).thenAnswer(invocation -> {
            Funcionarios f = invocation.getArgument(0);
            f.setId(1L);
            return f;
        });

        Funcionarios novo = funcionariosServices.saveFuncionarios(dto);
        assertNotNull(novo.getId());
        assertEquals("hash", novo.getSenha());
    }

    @Test
    void deveLancarExcecao_QuandoLoginDuplicado() {
        FuncionarioCreateDTO dto = new FuncionarioCreateDTO("Carlos", "RH", "carlos", "123");
        when(funcionariosRepository.findByLogin("carlos")).thenReturn(new Funcionarios());

        assertThrows(DuplicatedTupleException.class, () -> funcionariosServices.saveFuncionarios(dto));
    }

    @Test
    void deveAutenticarFuncionarioComSenhaCorreta() {
        Funcionarios funcionario = new Funcionarios(1L, "Maria", "TI", "maria", "hash", null, null);
        when(funcionariosRepository.findByLogin("maria")).thenReturn(funcionario);
        when(passwordEncoder.matches("senha", "hash")).thenReturn(true);
        when(jwt.getAccessToken(funcionario)).thenReturn(new AccessToken("token"));

        AccessToken token = funcionariosServices.authenticate("maria", "senha");
        assertNotNull(token);
    }

    @Test
    void naoDeveAutenticarComSenhaIncorreta() {
        Funcionarios funcionario = new Funcionarios(1L, "Maria", "TI", "maria", "hash", null, null);
        when(funcionariosRepository.findByLogin("maria")).thenReturn(funcionario);
        when(passwordEncoder.matches("senha", "hash")).thenReturn(false);

        AccessToken token = funcionariosServices.authenticate("maria", "senha");
        assertNull(token);
    }

    @Test
    void deveRetornarFuncionarioPorLogin() {
        Funcionarios funcionario = new Funcionarios(1L, "João", "TI", "joao", "senha", null, null);
        when(funcionariosRepository.findByLogin("joao")).thenReturn(funcionario);

        Funcionarios result = funcionariosServices.findUserByLogin("joao");
        assertEquals("João", result.getNome());
    }
}
