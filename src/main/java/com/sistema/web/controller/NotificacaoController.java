package com.sistema.web.controller;

import com.sistema.domain.services.NotificacaoService;
import com.sistema.web.dto.NotificacaoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/notificacoes")
@RequiredArgsConstructor
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    @GetMapping("/funcionario/{funcionarioId}")
    public ResponseEntity<List<NotificacaoDTO>> getNotificacoesByFuncionario(@PathVariable Long funcionarioId) {
        return ResponseEntity.ok(notificacaoService.findByFuncionarioId(funcionarioId));
    }

    @GetMapping("/funcionario/{funcionarioId}/nao-lidas")
    public ResponseEntity<List<NotificacaoDTO>> getNotificacoesNaoLidas(@PathVariable Long funcionarioId) {
        return ResponseEntity.ok(notificacaoService.findNaoLidasByFuncionarioId(funcionarioId));
    }

    @GetMapping("/funcionario/{funcionarioId}/count")
    public ResponseEntity<Map<String, Long>> countNaoLidas(@PathVariable Long funcionarioId) {
        long count = notificacaoService.countNaoLidas(funcionarioId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PutMapping("/{id}/lida")
    public ResponseEntity<Void> marcarComoLida(@PathVariable Long id) {
        notificacaoService.marcarComoLida(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/funcionario/{funcionarioId}/lidas")
    public ResponseEntity<Void> marcarTodasComoLidas(@PathVariable Long funcionarioId) {
        notificacaoService.marcarTodasComoLidas(funcionarioId);
        return ResponseEntity.noContent().build();
    }
}
