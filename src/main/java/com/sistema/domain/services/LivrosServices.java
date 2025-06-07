package com.sistema.domain.services;

import com.sistema.domain.entities.Livros;
import com.sistema.domain.repositories.LivrosRepository;
import com.sistema.infrastructure.exceptions.NotFoundException;
import com.sistema.web.dto.Livros.LivroCreateDTO;
import com.sistema.web.dto.Livros.LivroListDTO;
import com.sistema.web.dto.Livros.LivroResponseDTO;
import com.sistema.web.dto.Livros.LivroUpdateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
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

        if (!livro.getIsbn().equals(livro.getIsbn())) {
            if (livrosRepository.existsByIsbn(livro.getIsbn())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "ISBN já cadastrado");
            }
            livro.setIsbn(livro.getIsbn());
        }

        livro.setTitulo(dto.getTitulo());
        livro.setAutor(dto.getAutor());
        livro.setCategoria(dto.getCategoria());
        livro.setDisponibilidade(dto.getDisponibilidade());
        livro.setQuantidade(dto.getQuantidade());

        var updatedLivro = livrosRepository.save(livro);

        return LivroResponseDTO.converter(updatedLivro);
    }

    public List<LivroListDTO> findAllList() {
        return livrosRepository.findAll()
                .stream()
                .filter(livro -> livro.getQuantidade() > 0)
                .map(LivroListDTO::converter)
                .collect(Collectors.toList());
    }

    public LivroResponseDTO create(LivroCreateDTO dto) {
        if (livrosRepository.existsByIsbn(dto.getIsbn())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ISBN já cadastrado");
        }

        Livros livro = new Livros();
        livro.setTitulo(dto.getTitulo());
        livro.setAutor(dto.getAutor());
        livro.setCategoria(dto.getCategoria());
        livro.setDisponibilidade(dto.getDisponibilidade());
        livro.setIsbn(dto.getIsbn());
        livro.setQuantidade(dto.getQuantidade());

        Livros savedLivro = livrosRepository.save(livro);

        return LivroResponseDTO.converter(savedLivro);
    }

    public Livros findEntityById(Long id) {
        return livrosRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Livro não encontrado com o ID: " + id));
    }

}
