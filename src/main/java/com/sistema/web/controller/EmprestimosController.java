package com.sistema.web.controller;

import com.sistema.domain.services.EmprestimosServices;
import com.sistema.web.dto.Emprestimos.EmprestimoCreateDTO;
import com.sistema.web.dto.Emprestimos.EmprestimoResponseDTO;
import com.sistema.web.dto.Emprestimos.EmprestimoUpdateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/emprestimos")
@Slf4j
public class EmprestimosController {

    private final EmprestimosServices emprestimosServices;

    public EmprestimosController(EmprestimosServices emprestimosServices) {
        this.emprestimosServices = emprestimosServices;
    }

    @GetMapping
    public ResponseEntity<Page<EmprestimoResponseDTO>> getAllEmprestimos(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<EmprestimoResponseDTO> emprestimos = emprestimosServices.findAll(pageable);
        return ResponseEntity.ok(emprestimos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmprestimoResponseDTO> getEmprestimoById(@PathVariable Long id) {
        EmprestimoResponseDTO emprestimo = emprestimosServices.findById(id);
        return ResponseEntity.ok(emprestimo);
    }

    @GetMapping("/membro/{membroId}")
    public ResponseEntity<Page<EmprestimoResponseDTO>> getHistoricoByMembroId(
            @PathVariable Long membroId,
            @PageableDefault(page = 0, size = 10, sort = "dataEmprestimo", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(emprestimosServices.findByMembroId(membroId, pageable));
    }

    @PutMapping
    public ResponseEntity<EmprestimoResponseDTO> updateEmprestimo(@RequestBody EmprestimoUpdateDTO dto) {
        EmprestimoResponseDTO updatedEmprestimo = emprestimosServices.update(dto);
        return ResponseEntity.ok(updatedEmprestimo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmprestimo(@PathVariable Long id) {
        emprestimosServices.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<EmprestimoResponseDTO> createEmprestimo(@RequestBody EmprestimoCreateDTO dto) {
        EmprestimoResponseDTO createdEmprestimo = emprestimosServices.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmprestimo);
    }

}
