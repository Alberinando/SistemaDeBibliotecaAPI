package com.sistema.domain.repositories;

import com.sistema.domain.entities.Emprestimos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmprestimosRepository extends JpaRepository<Emprestimos,Long> {
}
