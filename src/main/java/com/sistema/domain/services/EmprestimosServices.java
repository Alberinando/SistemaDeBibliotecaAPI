package com.sistema.domain.services;

import com.sistema.domain.repositories.EmprestimosRepository;
import com.sistema.infrastructure.exceptions.NotFoundException;
import com.sistema.web.dto.Emprestimos.EmprestimoResponseDTO;
import com.sistema.web.dto.Emprestimos.EmprestimoUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class EmprestimosServices {

    private final EmprestimosRepository emprestimosRepository;

    public EmprestimosServices(EmprestimosRepository emprestimosRepository) {
        this.emprestimosRepository = emprestimosRepository;
    }

    public Page<EmprestimoResponseDTO> findAll(Pageable pageable) {
        return emprestimosRepository.findAll(pageable)
                .map(emprestimos -> {
                    EmprestimoResponseDTO dto = new EmprestimoResponseDTO();
                    dto.setId(emprestimos.getId());
                    dto.setLivros(emprestimos.getLivro());
                    dto.setMembros(emprestimos.getMembro());
                    dto.setDataEmprestimo(emprestimos.getDataEmprestimo());
                    dto.setDataDevolucao(emprestimos.getDataDevolucao());
                    dto.setStatus(emprestimos.getStatus());
                    return dto;
                });
    }

    public EmprestimoResponseDTO findById(Long id) {
        return emprestimosRepository.findById(id)
                .map(EmprestimoResponseDTO::converter)
                .orElseThrow(() -> new NotFoundException("Empréstimo não encontrado!"));
    }

    public void delete(Long id) {
        if (!emprestimosRepository.existsById(id)) {
            throw new NotFoundException("Empréstimo não encontrado!");
        }
        emprestimosRepository.deleteById(id);
    }

    public EmprestimoResponseDTO update(EmprestimoUpdateDTO dto) {
        var emprestimo = emprestimosRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Empréstimo não encontrado!"));

        emprestimo.setLivro(dto.getLivros());
        emprestimo.setMembro(dto.getMembros());
        emprestimo.setDataEmprestimo(dto.getDataEmprestimo());
        emprestimo.setDataDevolucao(dto.getDataDevolucao());
        emprestimo.setStatus(dto.getStatus());

        var updatedEmprestimo = emprestimosRepository.save(emprestimo);
        return EmprestimoResponseDTO.converter(updatedEmprestimo);
    }
}
