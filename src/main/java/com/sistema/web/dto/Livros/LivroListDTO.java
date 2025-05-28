package com.sistema.web.dto.Livros;

import com.sistema.domain.entities.Livros;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LivroListDTO {
    private Long id;
    private String titulo;

    public LivroListDTO(Livros livros) {
        this.id = livros.getId();
        this.titulo = livros.getTitulo();
    }

    public static LivroListDTO converter(Livros livros){
        return new LivroListDTO(livros);
    }
}