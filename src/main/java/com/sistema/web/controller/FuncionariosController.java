package com.sistema.web.controller;

import com.sistema.domain.services.FuncionariosServices;
import com.sistema.web.dto.CredentialsDTO;
import com.sistema.web.dto.Funcionarios.FuncionarioCreateDTO;
import com.sistema.web.dto.Funcionarios.FuncionarioResponseDTO;
import com.sistema.web.dto.Funcionarios.FuncionarioUpdateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/funcionario")
@RequiredArgsConstructor
@Slf4j
public class FuncionariosController {

    private final FuncionariosServices funcionariosServices;

    @GetMapping
    public ResponseEntity<Page<FuncionarioResponseDTO>> getAllFuncionarios(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
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

    @PostMapping
    public ResponseEntity save(@RequestBody FuncionarioCreateDTO funcionarioCreateDTO) {
        try {
            funcionariosServices.saveFuncionarios(funcionarioCreateDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            Map<String, String> jsonResult = Map.of("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(jsonResult);
        }
    }

    @PostMapping("/auth")
    public ResponseEntity aurhentication(@RequestBody CredentialsDTO credentialsDTO) {
        var token = funcionariosServices.authenticate(credentialsDTO.getLogin(), credentialsDTO.getSenha());
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok().body(token);
    }
}
