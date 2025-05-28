package com.sistema.web.dto.Historico;

import com.sistema.domain.entities.Historico;
import com.sistema.domain.entities.Livros;
import com.sistema.domain.entities.Membros;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoResponseDTO {
    private Long id;
    private Livros livros;
    private Membros membros;
    private LocalDateTime dataAcao;

    public HistoricoResponseDTO(Historico historico){
        this.id = historico.getId();
        this.livros = historico.getLivros();
        this.membros = historico.getMembros();
        this.dataAcao = historico.getDataAcao();
    }

    public static HistoricoResponseDTO converter(Historico historico){
        return new HistoricoResponseDTO(historico);
    }
}