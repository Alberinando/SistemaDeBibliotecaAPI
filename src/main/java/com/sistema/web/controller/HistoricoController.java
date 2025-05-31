package com.sistema.web.controller;

import com.sistema.domain.services.HistoricoServices;
import com.sistema.web.dto.Historico.HistoricoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/historico")
public class HistoricoController {

    private final HistoricoServices historicoServices;

    public HistoricoController(HistoricoServices historicoServices) {
        this.historicoServices = historicoServices;
    }

    @GetMapping
    public ResponseEntity<Page<HistoricoResponseDTO>> getAllHistoricos(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<HistoricoResponseDTO> historicos = historicoServices.findAll(pageable);
        return ResponseEntity.ok(historicos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistoricoResponseDTO> getHistoricoById(@PathVariable Long id) {
        HistoricoResponseDTO historico = historicoServices.findById(id);
        return ResponseEntity.ok(historico);
    }
}
