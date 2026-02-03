package com.sistema.web.dto;

import com.sistema.domain.entities.Notificacao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacaoDTO {
    private Long id;
    private Long funcionarioId;
    private Long emprestimoId;
    private String tituloLivro;
    private String nomeMembro;
    private String mensagem;
    private Boolean lida;
    private LocalDateTime createdAt;

    public static NotificacaoDTO converter(Notificacao notificacao) {
        NotificacaoDTO dto = new NotificacaoDTO();
        dto.setId(notificacao.getId());
        dto.setFuncionarioId(notificacao.getFuncionario().getId());
        dto.setEmprestimoId(notificacao.getEmprestimo().getId());
        dto.setTituloLivro(notificacao.getEmprestimo().getLivro().getTitulo());
        dto.setNomeMembro(notificacao.getEmprestimo().getMembro().getNome());
        dto.setMensagem(notificacao.getMensagem());
        dto.setLida(notificacao.getLida());
        dto.setCreatedAt(notificacao.getCreatedAt());
        return dto;
    }
}
