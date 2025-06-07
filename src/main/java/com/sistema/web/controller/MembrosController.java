package com.sistema.web.controller;

import com.sistema.domain.services.MembrosServices;
import com.sistema.web.dto.Membros.MembroListDTO;
import com.sistema.web.dto.Membros.MembrosCreateDTO;
import com.sistema.web.dto.Membros.MembrosResponseDTO;
import com.sistema.web.dto.Membros.MembrosUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/membros")
@CrossOrigin("*")
public class MembrosController {

    private final MembrosServices membrosServices;

    public MembrosController(MembrosServices membrosServices) {
        this.membrosServices = membrosServices;
    }

    @GetMapping
    public ResponseEntity<Page<MembrosResponseDTO>> findAll(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<MembrosResponseDTO> page = membrosServices.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MembrosResponseDTO> findById(@PathVariable Long id) {
        MembrosResponseDTO membro = membrosServices.findById(id);
        return ResponseEntity.ok(membro);
    }

    @GetMapping("/list")
    public ResponseEntity<List<MembroListDTO>> findAllList() {
        List<MembroListDTO> lista = membrosServices.findAllList();
        return ResponseEntity.ok(lista);
    }

    @PutMapping
    public ResponseEntity<MembrosResponseDTO> update(@RequestBody MembrosUpdateDTO updateDTO) {
        MembrosResponseDTO updatedMembro = membrosServices.update(updateDTO);
        return ResponseEntity.ok(updatedMembro);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        membrosServices.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<MembrosResponseDTO> createMembro(@RequestBody MembrosCreateDTO dto) {
        MembrosResponseDTO createdMembro = membrosServices.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMembro);
    }
}
