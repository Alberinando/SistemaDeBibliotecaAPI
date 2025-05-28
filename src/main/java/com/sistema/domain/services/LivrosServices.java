package com.sistema.domain.services;

import com.sistema.domain.repositories.LivrosRepository;
import com.sistema.infrastructure.exceptions.NotFoundException;
import com.sistema.web.dto.Livros.LivroListDTO;
import com.sistema.web.dto.Livros.LivroResponseDTO;
import com.sistema.web.dto.Livros.LivroUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LivrosServices {

    private final LivrosRepository livrosRepository;

    public LivrosServices(LivrosRepository livrosRepository) {
        this.livrosRepository = livrosRepository;
    }

    public Page<LivroResponseDTO> findAll(Pageable pageable) {
        return livrosRepository.findAll(pageable)
                .map(livro -> {
                    LivroResponseDTO dto = new LivroResponseDTO();
                    dto.setId(livro.getId());
                    dto.setTitulo(livro.getTitulo());
                    dto.setAutor(livro.getAutor());
                    dto.setCategoria(livro.getCategoria());
                    dto.setDisponibilidade(livro.getDisponibilidade());
                    dto.setIsbn(livro.getIsbn());
                    return dto;
                });
    }

    public LivroResponseDTO findById(Long id){
        return livrosRepository.findById(id)
                .map(LivroResponseDTO::converter)
                .orElseThrow(() -> new NotFoundException("Livro não encontrado"));
    }

    public void delete(Long id) {
        if (!livrosRepository.existsById(id)) {
            throw new NotFoundException("Livro não encontrado");
        }
        livrosRepository.deleteById(id);
    }

    public LivroResponseDTO update(LivroUpdateDTO dto) {
        var livro = livrosRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Livro não encontrado"));

        livro.setTitulo(dto.getTitulo());
        livro.setAutor(dto.getAutor());
        livro.setCategoria(dto.getCategoria());
        livro.setDisponibilidade(dto.getDisponibilidade());

        var updatedLivro = livrosRepository.save(livro);

        return LivroResponseDTO.converter(updatedLivro);
    }

    public List<LivroListDTO> findAllList() {
        return livrosRepository.findAll()
                .stream()
                .map(LivroListDTO::converter)
                .collect(Collectors.toList());
    }
}
