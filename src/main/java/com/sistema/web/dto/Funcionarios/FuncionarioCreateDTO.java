package com.sistema.web.dto.Funcionarios;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FuncionarioCreateDTO {
    private String nome;
    private String cargo;
    private String login;
    private String senha;
}