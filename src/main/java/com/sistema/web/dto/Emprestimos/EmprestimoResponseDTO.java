package com.sistema.web.dto.Emprestimos;

import com.sistema.domain.entities.Emprestimos;
import com.sistema.domain.entities.Livros;
import com.sistema.domain.entities.Membros;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmprestimoResponseDTO {
    private Long id;
    private Livros livros;
    private Membros membros;
    private LocalDateTime dataEmprestimo;
    private LocalDateTime dataDevolucao;
    private Boolean status;
    private Integer quantidade;

    public EmprestimoResponseDTO(Emprestimos emprestimo) {
        this.id = emprestimo.getId();
        this.livros = emprestimo.getLivro();
        this.membros = emprestimo.getMembro();
        this.dataEmprestimo = emprestimo.getDataEmprestimo();
        this.dataDevolucao = emprestimo.getDataDevolucao();
        this.status = emprestimo.getStatus();
        this.quantidade = emprestimo.getQuantidade();
    }

    public static EmprestimoResponseDTO converter(Emprestimos emprestimo) {
        return new EmprestimoResponseDTO(emprestimo);
    }
}
