package com.sistema.domain.services;

import com.sistema.domain.repositories.HistoricoRepository;
import com.sistema.infrastructure.exceptions.NotFoundException;
import com.sistema.web.dto.Historico.HistoricoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class HistoricoServices {

    private final HistoricoRepository historicoRepository;

    public HistoricoServices(HistoricoRepository historicoRepository) {
        this.historicoRepository = historicoRepository;
    }

    public Page<HistoricoResponseDTO> findAll(Pageable pageable) {
        return historicoRepository.findAll(pageable)
                .map(historico -> {
                    HistoricoResponseDTO dto = new HistoricoResponseDTO();
                    dto.setId(historico.getId());
                    dto.setLivros(historico.getLivros());
                    dto.setMembros(historico.getMembros());
                    dto.setDataAcao(historico.getDataAcao());
                    return dto;
                });
    }

    public HistoricoResponseDTO findById(Long id){
        return historicoRepository.findById(id)
                .map(HistoricoResponseDTO::converter)
                .orElseThrow(() -> new NotFoundException("Histórico não encontrado"));
    }
}
