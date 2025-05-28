package com.sistema.web.dto.Membros;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembrosUpdateDTO {
    private Long id;
    private String nome;
    private Long cpf;
    private Long telefone;
    private String email;
}