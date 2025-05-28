package com.sistema.web.dto.Membros;

import com.sistema.domain.entities.Membros;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembrosResponseDTO {
    private Long id;
    private String nome;
    private Long cpf;
    private Long telefone;
    private String email;

    public MembrosResponseDTO(Membros membros){
        this.id = membros.getId();
        this.nome = membros.getNome();
        this.cpf = membros.getCpf();
        this.telefone = membros.getTelefone();
        this.email = membros.getEmail();
    }

    public static MembrosResponseDTO converter(Membros membros){
        return new MembrosResponseDTO(membros);
    }
}