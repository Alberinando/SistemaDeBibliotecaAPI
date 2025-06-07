package com.sistema.domain.services;

import com.sistema.domain.entities.Funcionarios;
import com.sistema.domain.entities.Membros;
import com.sistema.domain.repositories.FuncionariosRepository;
import com.sistema.infrastructure.config.AccessToken;
import com.sistema.infrastructure.exceptions.DuplicatedTupleException;
import com.sistema.infrastructure.exceptions.NotFoundException;
import com.sistema.infrastructure.security.Jwt.Jwt;
import com.sistema.web.dto.Funcionarios.FuncionarioCreateDTO;
import com.sistema.web.dto.Funcionarios.FuncionarioResponseDTO;
import com.sistema.web.dto.Funcionarios.FuncionarioUpdateDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FuncionariosServices {

    private final PasswordEncoder passwordEncoder;
    private final FuncionariosRepository funcionariosRepository;
    private final Jwt token;

    public Page<FuncionarioResponseDTO> findAll(Pageable pageable) {
        return funcionariosRepository.findAll(pageable)
                .map(funcionario -> {
                    FuncionarioResponseDTO dto = new FuncionarioResponseDTO();
                    dto.setId(funcionario.getId());
                    dto.setNome(funcionario.getNome());
                    dto.setCargo(funcionario.getCargo());
                    dto.setLogin(funcionario.getLogin());
                    return dto;
                });
    }

    public FuncionarioResponseDTO findById(Long id){
        return funcionariosRepository.findById(id)
                .map(FuncionarioResponseDTO::converter)
                .orElseThrow(() -> new NotFoundException("Funcionário não encontrado"));
    }

    public void delete(Long id) {
        if (!funcionariosRepository.existsById(id)) {
            throw new NotFoundException("Funcionário não encontrado");
        }
        funcionariosRepository.deleteById(id);
    }

    public FuncionarioResponseDTO update(FuncionarioUpdateDTO dto) {
        var funcionario = funcionariosRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Funcionário não encontrado"));

        funcionario.setNome(dto.getNome());
        funcionario.setCargo(dto.getCargo());
        funcionario.setLogin(dto.getLogin());

        var updatedFuncionario = funcionariosRepository.save(funcionario);
        return FuncionarioResponseDTO.converter(updatedFuncionario);
    }

    public AccessToken authenticate(String login, String senha) {
        var funcionario = funcionariosRepository.findByLogin(login);
        if (funcionario == null) {
            return null;
        }

        boolean matches = passwordEncoder.matches(senha, funcionario.getSenha());

        if (matches) {
            return token.getAccessToken(funcionario);
        }

        return null;
    }

    public Funcionarios findUserByLogin(String login) {
        return funcionariosRepository.findByLogin(login);
    }

    @Transactional
    public Funcionarios saveFuncionarios(FuncionarioCreateDTO funcionarioCreateDTO) {
        var possibleFuncionario = findUserByLogin(funcionarioCreateDTO.getLogin());

        if (possibleFuncionario != null) {
            throw new DuplicatedTupleException("Funcionario já criado");
        }

        encodePassword(funcionarioCreateDTO);
        Funcionarios funcionario = new Funcionarios();
        funcionario.setNome(funcionarioCreateDTO.getNome());
        funcionario.setCargo(funcionarioCreateDTO.getCargo());
        funcionario.setLogin(funcionarioCreateDTO.getLogin());
        funcionario.setSenha(funcionarioCreateDTO.getSenha());

        return funcionariosRepository.save(funcionario);
    }

    private void encodePassword(FuncionarioCreateDTO funcionarioCreateDTO) {
        String hashedPassword = passwordEncoder.encode(funcionarioCreateDTO.getLogin());
        funcionarioCreateDTO.setSenha(hashedPassword);
    }
}
