package com.sistema.domain.repositories;

import com.sistema.domain.entities.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    List<Notificacao> findByFuncionarioIdAndLidaFalseOrderByCreatedAtDesc(Long funcionarioId);

    List<Notificacao> findByFuncionarioIdOrderByCreatedAtDesc(Long funcionarioId);

    long countByFuncionarioIdAndLidaFalse(Long funcionarioId);
}
