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
    @JoinColumn(name = "livroid", nullable = false)
    private Livros livro;

    @ManyToOne
    @JoinColumn(name = "membroid", nullable = false)
    private Membros membro;

    @Column(name = "dataemprestimo", nullable = false)
    private LocalDateTime dataEmprestimo = LocalDateTime.now();

    @Column(name = "datadevolucao")
    private LocalDateTime dataDevolucao;

    @Column(nullable = false, length = 20)
    private Boolean status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

}