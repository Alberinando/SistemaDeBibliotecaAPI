package com.sistema.web.dto.Livros;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LivroUpdateDTO {
    private Long id;
    private String titulo;
    private String autor;
    private String categoria;
    private Boolean disponibilidade;
}