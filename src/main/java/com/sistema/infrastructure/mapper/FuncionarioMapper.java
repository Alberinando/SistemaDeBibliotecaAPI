package com.sistema.infrastructure.mapper;

import com.sistema.domain.entities.Funcionarios;
import com.sistema.web.dto.Funcionarios.FuncionarioCreateDTO;
import org.springframework.stereotype.Component;

@Component
public class FuncionarioMapper {

    public Funcionarios mapToFuncionario(FuncionarioCreateDTO dto) {
        return Funcionarios.builder()
                .nome(dto.getNome())
                .cargo(dto.getCargo())
                .login(dto.getLogin())
                .senha(dto.getSenha())
                .build();
    }

}
