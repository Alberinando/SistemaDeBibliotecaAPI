package com.sistema.web.dto.Historico;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoListDTO {
    private Long id;
    private Long livroId;
    private Long idMembro;
}