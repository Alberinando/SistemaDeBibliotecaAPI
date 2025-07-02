package com.sistema.web.controller;

import com.sistema.domain.services.LivrosServices;
import com.sistema.web.dto.Livros.LivroCreateDTO;
import com.sistema.web.dto.Livros.LivroListDTO;
import com.sistema.web.dto.Livros.LivroResponseDTO;
import com.sistema.web.dto.Livros.LivroUpdateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/livros")
@CrossOrigin("*")
@Slf4j
public class LivrosController {
    private final LivrosServices livrosServices;

    public LivrosController(LivrosServices livrosServices) {
        this.livrosServices = livrosServices;
    }

    @GetMapping
    public ResponseEntity<Page<LivroResponseDTO>> getAllLivros(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<LivroResponseDTO> livros = livrosServices.findAll(pageable);
        return ResponseEntity.ok(livros);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LivroResponseDTO> getLivroById(@PathVariable Long id) {
        LivroResponseDTO livro = livrosServices.findById(id);
        return ResponseEntity.ok(livro);
    }

    @GetMapping("/list")
    public ResponseEntity<List<LivroListDTO>> getLivroList() {
        List<LivroListDTO> livros = livrosServices.findAllList();
        return ResponseEntity.ok(livros);
    }

    @PutMapping
    public ResponseEntity<LivroResponseDTO> updateLivro(@RequestBody LivroUpdateDTO dto) {
        LivroResponseDTO updatedLivro = livrosServices.update(dto);
        return ResponseEntity.ok(updatedLivro);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLivro(@PathVariable Long id) {
        livrosServices.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LivroResponseDTO createLivro(@RequestBody LivroCreateDTO dto) {
        return livrosServices.create(dto);
    }
}
