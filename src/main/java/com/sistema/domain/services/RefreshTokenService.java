package com.sistema.domain.services;

import com.sistema.domain.entities.Funcionarios;
import com.sistema.domain.entities.RefreshToken;
import com.sistema.domain.repositories.RefreshTokenRepository;
import com.sistema.infrastructure.exceptions.InvalidTokenException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${refresh-token.expiration-days:7}")
    private int expirationDays;

    /**
     * Cria um novo refresh token para o funcionário.
     * Revoga todos os tokens anteriores do funcionário (single session).
     */
    @Transactional
    public RefreshToken createRefreshToken(Funcionarios funcionario) {
        // Revoga todos os refresh tokens anteriores deste funcionário
        refreshTokenRepository.revokeAllByFuncionarioId(funcionario.getId());

        RefreshToken refreshToken = RefreshToken.builder()
                .token(generateToken())
                .funcionario(funcionario)
                .expiresAt(LocalDateTime.now().plusDays(expirationDays))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Valida e retorna o refresh token.
     * 
     * @throws InvalidTokenException se o token for inválido, expirado ou revogado
     */
    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Refresh token não encontrado"));

        if (refreshToken.isRevoked()) {
            log.warn("Tentativa de uso de refresh token revogado: {}", token.substring(0, 8));
            throw new InvalidTokenException("Refresh token foi revogado");
        }

        if (refreshToken.isExpired()) {
            log.warn("Tentativa de uso de refresh token expirado");
            throw new InvalidTokenException("Refresh token expirado");
        }

        return refreshToken;
    }

    /**
     * Realiza a rotação do refresh token:
     * - Revoga o token atual
     * - Gera um novo token
     */
    @Transactional
    public RefreshToken rotateRefreshToken(RefreshToken oldToken) {
        // Revoga o token antigo
        oldToken.setRevoked(true);
        refreshTokenRepository.save(oldToken);

        // Cria um novo token
        return createRefreshTokenWithoutRevoke(oldToken.getFuncionario());
    }

    /**
     * Revoga um refresh token específico (logout).
     */
    @Transactional
    public void revokeToken(String token) {
        refreshTokenRepository.findByToken(token)
                .ifPresent(rt -> {
                    rt.setRevoked(true);
                    refreshTokenRepository.save(rt);
                    log.info("Refresh token revogado para funcionário: {}", rt.getFuncionario().getLogin());
                });
    }

    /**
     * Revoga todos os refresh tokens de um funcionário (logout de todas as
     * sessões).
     */
    @Transactional
    public void revokeAllTokens(Long funcionarioId) {
        refreshTokenRepository.revokeAllByFuncionarioId(funcionarioId);
        log.info("Todos os refresh tokens revogados para funcionário ID: {}", funcionarioId);
    }

    /**
     * Job agendado para limpar tokens expirados (roda diariamente às 3h).
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteAllExpired();
        log.info("Tokens expirados removidos do banco de dados");
    }

    private RefreshToken createRefreshTokenWithoutRevoke(Funcionarios funcionario) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(generateToken())
                .funcionario(funcionario)
                .expiresAt(LocalDateTime.now().plusDays(expirationDays))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    private String generateToken() {
        return UUID.randomUUID().toString() + UUID.randomUUID().toString().replace("-", "");
    }
}
