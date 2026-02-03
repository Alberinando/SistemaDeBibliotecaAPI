package com.sistema.domain.services;

import com.sistema.domain.entities.Emprestimos;
import com.sistema.domain.entities.Livros;
import com.sistema.domain.entities.Membros;
import com.sistema.domain.repositories.EmprestimosRepository;
import com.sistema.domain.repositories.LivrosRepository;
import com.sistema.domain.repositories.MembrosRepository;
import com.sistema.infrastructure.exceptions.NotFoundException;
import com.sistema.web.dto.Emprestimos.EmprestimoCreateDTO;
import com.sistema.web.dto.Emprestimos.EmprestimoResponseDTO;
import com.sistema.web.dto.Emprestimos.EmprestimoUpdateDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmprestimosServices {

    private final EmprestimosRepository emprestimosRepository;
    private final LivrosServices livrosServices;
    private final MembrosServices membrosServices;
    private final LivrosRepository livrosRepository;
    private final MembrosRepository membrosRepository;

    public EmprestimosServices(EmprestimosRepository emprestimosRepository, LivrosServices livrosServices,
            MembrosServices membrosServices, LivrosRepository livrosRepository, MembrosRepository membrosRepository) {
        this.emprestimosRepository = emprestimosRepository;
        this.livrosServices = livrosServices;
        this.membrosServices = membrosServices;
        this.livrosRepository = livrosRepository;
        this.membrosRepository = membrosRepository;
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
                    dto.setQuantidade(emprestimos.getQuantidade());
                    return dto;
                });
    }

    public EmprestimoResponseDTO findById(Long id) {
        return emprestimosRepository.findById(id)
                .map(EmprestimoResponseDTO::converter)
                .orElseThrow(() -> new NotFoundException("Empréstimo não encontrado!"));
    }

    public Page<EmprestimoResponseDTO> findByMembroId(Long membroId, Pageable pageable) {
        return emprestimosRepository.findByMembroId(membroId, pageable)
                .map(emprestimo -> {
                    EmprestimoResponseDTO dto = new EmprestimoResponseDTO();
                    dto.setId(emprestimo.getId());
                    dto.setLivros(emprestimo.getLivro());
                    dto.setMembros(emprestimo.getMembro());
                    dto.setDataEmprestimo(emprestimo.getDataEmprestimo());
                    dto.setDataDevolucao(emprestimo.getDataDevolucao());
                    dto.setStatus(emprestimo.getStatus());
                    dto.setQuantidade(emprestimo.getQuantidade());
                    return dto;
                });
    }

    @Transactional
    public void delete(Long id) {
        var emprestimo = emprestimosRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Empréstimo não encontrado!"));

        // Se o empréstimo estava ativo, devolve a quantidade ao livro
        if (Boolean.TRUE.equals(emprestimo.getStatus())) {
            devolverQuantidadeAoLivro(emprestimo.getLivro(), emprestimo.getQuantidade());
        }

        emprestimosRepository.deleteById(id);
    }

    @Transactional
    public EmprestimoResponseDTO update(EmprestimoUpdateDTO dto) {
        var emprestimo = emprestimosRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Empréstimo não encontrado!"));

        Livros livro = livrosRepository.findById(dto.getLivros())
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado"));

        // Status atual antes da atualização
        Boolean statusAnterior = emprestimo.getStatus();

        emprestimo.setLivro(livro);

        Membros membro = membrosRepository.findById(dto.getMembros())
                .orElseThrow(() -> new EntityNotFoundException("Membro não encontrado"));
        emprestimo.setMembro(membro);

        emprestimo.setDataEmprestimo(dto.getDataEmprestimo());
        emprestimo.setDataDevolucao(dto.getDataDevolucao());
        emprestimo.setStatus(dto.getStatus());

        if (dto.getQuantidade() != null) {
            emprestimo.setQuantidade(dto.getQuantidade());
        }

        // Se o status mudou de ativo para encerrado (devolução), devolve a quantidade
        // ao livro
        if (Boolean.TRUE.equals(statusAnterior) && Boolean.FALSE.equals(dto.getStatus())) {
            devolverQuantidadeAoLivro(emprestimo.getLivro(), emprestimo.getQuantidade());
        }

        var updatedEmprestimo = emprestimosRepository.save(emprestimo);
        return EmprestimoResponseDTO.converter(updatedEmprestimo);
    }

    @Transactional
    public EmprestimoResponseDTO create(EmprestimoCreateDTO dto) {
        Livros livro = livrosServices.findEntityById(dto.getLivroId());
        Membros membro = membrosServices.findEntityById(dto.getMembroId());

        Integer quantidade = dto.getQuantidade() != null ? dto.getQuantidade() : 1;

        // Valida se há quantidade suficiente
        if (livro.getQuantidade() < quantidade) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Quantidade insuficiente. Disponível: " + livro.getQuantidade());
        }

        // Decrementa a quantidade do livro
        livro.setQuantidade(livro.getQuantidade() - quantidade);
        livrosRepository.save(livro);

        Emprestimos emprestimo = new Emprestimos();
        emprestimo.setLivro(livro);
        emprestimo.setMembro(membro);
        emprestimo.setDataEmprestimo(dto.getDataEmprestimo());
        emprestimo.setDataDevolucao(dto.getDataDevolucao());
        emprestimo.setStatus(dto.getStatus());
        emprestimo.setQuantidade(quantidade);

        Emprestimos savedEmprestimo = emprestimosRepository.save(emprestimo);
        return EmprestimoResponseDTO.converter(savedEmprestimo);
    }

    /**
     * Devolve a quantidade emprestada ao estoque do livro
     */
    private void devolverQuantidadeAoLivro(Livros livro, Integer quantidade) {
        livro.setQuantidade(livro.getQuantidade() + quantidade);
        livrosRepository.save(livro);
    }
}
