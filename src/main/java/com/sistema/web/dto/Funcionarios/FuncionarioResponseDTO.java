package com.sistema.web.dto.Funcionarios;

import com.sistema.domain.entities.Funcionarios;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FuncionarioResponseDTO {
    private Long id;
    private String nome;
    private String cargo;
    private String login;

    public FuncionarioResponseDTO(Funcionarios funcionario){
        this.id = funcionario.getId();
        this.nome = funcionario.getNome();
        this.cargo = funcionario.getCargo();
        this.login = funcionario.getLogin();
    }

    public static FuncionarioResponseDTO converter(Funcionarios funcionario){
        return new FuncionarioResponseDTO(funcionario);
    }
}