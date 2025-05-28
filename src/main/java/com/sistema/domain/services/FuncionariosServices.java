package com.sistema.domain.services;

import com.sistema.domain.repositories.FuncionariosRepository;
import com.sistema.infrastructure.exceptions.NotFoundException;
import com.sistema.web.dto.Funcionarios.FuncionarioResponseDTO;
import com.sistema.web.dto.Funcionarios.FuncionarioUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FuncionariosServices {

    private final FuncionariosRepository funcionariosRepository;

    public FuncionariosServices(FuncionariosRepository funcionariosRepository) {
        this.funcionariosRepository = funcionariosRepository;
    }

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
}
