package com.sistema.domain.services;

import com.sistema.domain.entities.Membros;
import com.sistema.domain.repositories.MembrosRepository;
import com.sistema.infrastructure.exceptions.NotFoundException;
import com.sistema.web.dto.Membros.MembroListDTO;
import com.sistema.web.dto.Membros.MembrosCreateDTO;
import com.sistema.web.dto.Membros.MembrosResponseDTO;
import com.sistema.web.dto.Membros.MembrosUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MembrosServices {

    private final MembrosRepository membrosRepository;

    public MembrosServices(MembrosRepository membrosRepository) {
        this.membrosRepository = membrosRepository;
    }

    public Page<MembrosResponseDTO> findAll(Pageable pageable) {
        return membrosRepository.findAll(pageable)
                .map(membro -> {
                    MembrosResponseDTO dto = new MembrosResponseDTO();
                    dto.setId(membro.getId());
                    dto.setNome(membro.getNome());
                    dto.setCpf(membro.getCpf());
                    dto.setTelefone(membro.getTelefone());
                    dto.setEmail(membro.getEmail());
                    return dto;
                });
    }

    public MembrosResponseDTO findById(Long id) {
        return membrosRepository.findById(id)
                .map(MembrosResponseDTO::converter)
                .orElseThrow(() -> new NotFoundException("Membro não encontrado!"));
    }

    public Membros findEntityById(Long id) {
        return membrosRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Membro não encontrado com o ID: " + id));
    }

    public void delete(Long id) {
        if (!membrosRepository.existsById(id)) {
            throw new NotFoundException("Membro não encontrado!");
        }
        membrosRepository.deleteById(id);
    }

    public MembrosResponseDTO update(MembrosUpdateDTO dto) {
        var membro = membrosRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Membro não encontrado!"));

        if (membrosRepository.existsByCpf(dto.getCpf())) {
            throw new IllegalArgumentException("CPF já existe!");
        }

        if (membrosRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email já existe!");
        }

        if(!Objects.equals(membro.getCpf(), dto.getCpf())) {
            membro.setCpf(dto.getCpf());
        }

        if(!Objects.equals(membro.getEmail(), dto.getEmail())) {
            membro.setEmail(dto.getEmail());
        }

        membro.setNome(dto.getNome());
        membro.setTelefone(dto.getTelefone());

        var updatedMembro = membrosRepository.save(membro);
        return MembrosResponseDTO.converter(updatedMembro);
    }

    public List<MembroListDTO> findAllList() {
        return membrosRepository.findAll()
                .stream()
                .map(MembroListDTO::converter)
                .collect(Collectors.toList());
    }

    public MembrosResponseDTO create(MembrosCreateDTO dto) {
        if (membrosRepository.existsByCpf(dto.getCpf())) {
            throw new IllegalArgumentException("CPF já existe!");
        }

        if (membrosRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email já existe!");
        }

        Membros membro = new Membros();
        membro.setNome(dto.getNome());
        membro.setCpf(dto.getCpf());
        membro.setTelefone(dto.getTelefone());
        membro.setEmail(dto.getEmail());

        Membros savedMembro = membrosRepository.save(membro);
        return MembrosResponseDTO.converter(savedMembro);
    }

}
