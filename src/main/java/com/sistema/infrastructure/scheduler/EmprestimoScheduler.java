package com.sistema.infrastructure.scheduler;

import com.sistema.domain.entities.Emprestimos;
import com.sistema.domain.entities.Funcionarios;
import com.sistema.domain.repositories.EmprestimosRepository;
import com.sistema.domain.repositories.FuncionariosRepository;
import com.sistema.domain.repositories.LivrosRepository;
import com.sistema.domain.services.NotificacaoService;
import com.sistema.infrastructure.websocket.NotificacaoWebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmprestimoScheduler {

    private final EmprestimosRepository emprestimosRepository;
    private final FuncionariosRepository funcionariosRepository;
    private final LivrosRepository livrosRepository;
    private final NotificacaoService notificacaoService;
    private final NotificacaoWebSocketService webSocketService;

    /**
     * Job executado todos os dias à meia-noite
     * Verifica empréstimos vencidos e toma ação baseada na preferência do
     * funcionário
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void verificarEmprestimosVencidos() {
        log.info("Iniciando verificação de empréstimos vencidos...");

        LocalDateTime agora = LocalDateTime.now();
        List<Emprestimos> emprestimosAtivos = emprestimosRepository.findAll()
                .stream()
                .filter(e -> Boolean.TRUE.equals(e.getStatus()))
                .filter(e -> e.getDataDevolucao() != null && e.getDataDevolucao().isBefore(agora))
                .toList();

        log.info("Encontrados {} empréstimos vencidos", emprestimosAtivos.size());

        // Busca todos os funcionários para verificar preferência
        List<Funcionarios> funcionarios = funcionariosRepository.findAll();

        for (Emprestimos emprestimo : emprestimosAtivos) {
            processarEmprestimoVencido(emprestimo, funcionarios);
        }

        log.info("Verificação de empréstimos vencidos concluída");
    }

    private void processarEmprestimoVencido(Emprestimos emprestimo, List<Funcionarios> funcionarios) {
        for (Funcionarios funcionario : funcionarios) {
            if (Boolean.TRUE.equals(funcionario.getNotificacaoAutomatica())) {
                // Devolução automática
                devolverAutomaticamente(emprestimo);
                notificarFuncionario(funcionario, emprestimo,
                        "Devolução automática: " + emprestimo.getLivro().getTitulo() +
                                " - Membro: " + emprestimo.getMembro().getNome());
            } else {
                // Apenas notifica via WebSocket
                notificarFuncionario(funcionario, emprestimo,
                        "Empréstimo vencido: " + emprestimo.getLivro().getTitulo() +
                                " - Membro: " + emprestimo.getMembro().getNome());

                // Envia via WebSocket
                webSocketService.enviarNotificacao(funcionario.getId(),
                        "Empréstimo vencido aguardando ação: " + emprestimo.getLivro().getTitulo());
            }
        }
    }

    @Transactional
    public void devolverAutomaticamente(Emprestimos emprestimo) {
        // Atualiza status para encerrado
        emprestimo.setStatus(false);
        emprestimosRepository.save(emprestimo);

        // Devolve quantidade ao livro
        var livro = emprestimo.getLivro();
        livro.setQuantidade(livro.getQuantidade() + emprestimo.getQuantidade());
        livrosRepository.save(livro);

        log.info("Devolução automática realizada para empréstimo ID: {}", emprestimo.getId());
    }

    private void notificarFuncionario(Funcionarios funcionario, Emprestimos emprestimo, String mensagem) {
        notificacaoService.criarNotificacao(funcionario, emprestimo, mensagem);
    }
}
