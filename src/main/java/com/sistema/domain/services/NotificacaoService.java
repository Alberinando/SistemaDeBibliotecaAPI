package com.sistema.domain.services;

import com.sistema.domain.entities.Emprestimos;
import com.sistema.domain.entities.Funcionarios;
import com.sistema.domain.entities.Notificacao;
import com.sistema.domain.repositories.NotificacaoRepository;
import com.sistema.web.dto.NotificacaoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;

    public List<NotificacaoDTO> findByFuncionarioId(Long funcionarioId) {
        return notificacaoRepository.findByFuncionarioIdOrderByCreatedAtDesc(funcionarioId)
                .stream()
                .map(NotificacaoDTO::converter)
                .collect(Collectors.toList());
    }

    public List<NotificacaoDTO> findNaoLidasByFuncionarioId(Long funcionarioId) {
        return notificacaoRepository.findByFuncionarioIdAndLidaFalseOrderByCreatedAtDesc(funcionarioId)
                .stream()
                .map(NotificacaoDTO::converter)
                .collect(Collectors.toList());
    }

    public long countNaoLidas(Long funcionarioId) {
        return notificacaoRepository.countByFuncionarioIdAndLidaFalse(funcionarioId);
    }

    @Transactional
    public void criarNotificacao(Funcionarios funcionario, Emprestimos emprestimo, String mensagem) {
        Notificacao notificacao = new Notificacao();
        notificacao.setFuncionario(funcionario);
        notificacao.setEmprestimo(emprestimo);
        notificacao.setMensagem(mensagem);
        notificacao.setLida(false);
        notificacaoRepository.save(notificacao);
    }

    @Transactional
    public void marcarComoLida(Long notificacaoId) {
        notificacaoRepository.findById(notificacaoId).ifPresent(n -> {
            n.setLida(true);
            notificacaoRepository.save(n);
        });
    }

    @Transactional
    public void marcarTodasComoLidas(Long funcionarioId) {
        var notificacoes = notificacaoRepository.findByFuncionarioIdAndLidaFalseOrderByCreatedAtDesc(funcionarioId);
        notificacoes.forEach(n -> n.setLida(true));
        notificacaoRepository.saveAll(notificacoes);
    }
}
