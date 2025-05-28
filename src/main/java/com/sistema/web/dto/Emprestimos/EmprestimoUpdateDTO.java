package com.sistema.web.dto.Emprestimos;

import com.sistema.domain.entities.Livros;
import com.sistema.domain.entities.Membros;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmprestimoUpdateDTO {
    private Long id;
    private Livros livros;
    private Membros membros;
    private LocalDateTime dataEmprestimo;
    private LocalDateTime dataDevolucao;
    private Boolean status;
}