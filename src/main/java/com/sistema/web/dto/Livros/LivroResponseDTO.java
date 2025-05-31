package com.sistema.web.dto.Livros;

import com.sistema.domain.entities.Historico;
import com.sistema.domain.entities.Livros;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LivroResponseDTO {
    private Long id;
    private String titulo;
    private String autor;
    private String categoria;
    private Boolean disponibilidade;
    private Long isbn;
    private Integer quantidade;

    public LivroResponseDTO(Livros Livros) {
        this.id = Livros.getId();
        this.titulo = Livros.getTitulo();
        this.autor = Livros.getAutor();
        this.categoria = Livros.getCategoria();
        this.disponibilidade = Livros.getDisponibilidade();
        this.isbn = Livros.getIsbn();
        this.quantidade = Livros.getQuantidade();
    }

    public static LivroResponseDTO converter(Livros Livros){
        return new LivroResponseDTO(Livros);
    }
}