package com.sistema.web.dto.Membros;

import com.sistema.domain.entities.Membros;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembroListDTO {
    private Long id;
    private String nome;

    public MembroListDTO(Membros membros) {
        this.id = membros.getId();
        this.nome = membros.getNome();
    }

    public static MembroListDTO converter(Membros membros){
        return new MembroListDTO(membros);
    }
}