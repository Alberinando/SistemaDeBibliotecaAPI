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
@Table(name = "historicoemprestimos")
public class Historico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "emprestimoid", nullable = false)
    private Long emprestimoId;

    @ManyToOne
    @JoinColumn(name = "livroid", nullable = false)
    private Livros livros;

    @ManyToOne
    @JoinColumn(name = "idmembro", nullable = false)
    private Membros membros;

    @Column(name = "dataacao", nullable = false)
    private LocalDateTime dataAcao = LocalDateTime.now();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}