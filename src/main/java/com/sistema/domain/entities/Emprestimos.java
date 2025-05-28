package com.sistema.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "emprestimos")
public class Emprestimos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "livroId", nullable = false)
    private Livros livro;

    @ManyToOne
    @JoinColumn(name = "membroId", nullable = false)
    private Membros membro;

    @Column(nullable = false)
    private LocalDateTime dataEmprestimo = LocalDateTime.now();

    private LocalDateTime dataDevolucao;

    @Column(nullable = false, length = 20)
    private Boolean status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

}