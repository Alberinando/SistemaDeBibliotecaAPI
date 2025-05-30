package com.sistema.domain.repositories;

import com.sistema.domain.entities.Livros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LivrosRepository extends JpaRepository<Livros, Long> {
    boolean existsByIsbn(Long isbn);
}
