package com.sistema.web.dto.Funcionarios;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FuncionarioUpdateDTO {
    private Long id;
    private String nome;
    private String cargo;
    private String login;
}