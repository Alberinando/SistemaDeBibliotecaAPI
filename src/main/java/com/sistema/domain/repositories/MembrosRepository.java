package com.sistema.domain.repositories;

import com.sistema.domain.entities.Membros;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembrosRepository extends JpaRepository<Membros, Long> {
    boolean existsByCpf(Long cpf);
    boolean existsByEmail(String email);
}
