package com.sistema.web.controller;

import com.sistema.domain.services.FuncionariosServices;
import com.sistema.web.dto.Funcionarios.FuncionarioResponseDTO;
import com.sistema.web.dto.Funcionarios.FuncionarioUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/funcionario")
public class FuncionariosController {

    private final FuncionariosServices funcionariosServices;

    public FuncionariosController(FuncionariosServices funcionariosServices) {
        this.funcionariosServices = funcionariosServices;
    }

    @GetMapping
    public ResponseEntity<Page<FuncionarioResponseDTO>> getAllFuncionarios(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<FuncionarioResponseDTO> funcionarios = funcionariosServices.findAll(pageable);
        return ResponseEntity.ok(funcionarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioResponseDTO> getFuncionarioById(@PathVariable Long id) {
        FuncionarioResponseDTO funcionario = funcionariosServices.findById(id);
        return ResponseEntity.ok(funcionario);
    }

    @PutMapping
    public ResponseEntity<FuncionarioResponseDTO> updateFuncionario(@RequestBody FuncionarioUpdateDTO dto) {
        FuncionarioResponseDTO updatedFuncionario = funcionariosServices.update(dto);
        return ResponseEntity.ok(updatedFuncionario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFuncionario(@PathVariable Long id) {
        funcionariosServices.delete(id);
        return ResponseEntity.noContent().build();
    }
}
