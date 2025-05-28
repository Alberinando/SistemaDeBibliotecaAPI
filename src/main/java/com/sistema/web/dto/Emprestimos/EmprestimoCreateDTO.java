package com.sistema.web.dto.Emprestimos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmprestimoCreateDTO {
    private Long livroId;
    private Long membroId;
    private LocalDateTime dataEmprestimo = LocalDateTime.now();
    private LocalDateTime dataDevolucao;
    private Boolean status;
}
