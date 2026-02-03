package com.sistema.web.dto.Emprestimos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmprestimoUpdateDTO {
    private Long id;
    private Long livros;
    private Long membros;
    private LocalDateTime dataEmprestimo;
    private LocalDateTime dataDevolucao;
    private Boolean status;
    private Integer quantidade;
}
