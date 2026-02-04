package com.sistema.domain.services;

import com.sistema.domain.entities.Funcionarios;
import com.sistema.domain.entities.RefreshToken;
import com.sistema.domain.repositories.FuncionariosRepository;
import com.sistema.infrastructure.config.AccessToken;
import com.sistema.infrastructure.exceptions.DuplicatedTupleException;
import com.sistema.infrastructure.exceptions.NotFoundException;
import com.sistema.infrastructure.security.Jwt.Jwt;
import com.sistema.web.dto.AuthResponseDTO;
import com.sistema.web.dto.Funcionarios.ChangePasswordDTO;
import com.sistema.web.dto.Funcionarios.FuncionarioCreateDTO;
import com.sistema.web.dto.Funcionarios.FuncionarioResponseDTO;
import com.sistema.web.dto.Funcionarios.FuncionarioUpdateDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

@Service
@RequiredArgsConstructor
@Slf4j
public class FuncionariosServices {

    private final PasswordEncoder passwordEncoder;
    private final FuncionariosRepository funcionariosRepository;
    private final Jwt jwt;
    private final RefreshTokenService refreshTokenService;

    public Page<FuncionarioResponseDTO> findAll(Pageable pageable) {
        Funcionarios authenticatedUser = getAuthenticatedFuncionario();
        Page<Funcionarios> page;

        if (authenticatedUser.getId() != 1L) {
            page = funcionariosRepository.findByIdNot(1L, pageable);
        } else {
            page = funcionariosRepository.findAll(pageable);
        }

        return page.map(funcionario -> {
            FuncionarioResponseDTO dto = new FuncionarioResponseDTO();
            dto.setId(funcionario.getId());
            dto.setNome(funcionario.getNome());
            dto.setCargo(funcionario.getCargo());
            dto.setLogin(funcionario.getLogin());
            dto.setNotificacaoAutomatica(funcionario.getNotificacaoAutomatica());
            return dto;
        });
    }

    public FuncionarioResponseDTO findById(Long id) {
        checkPrivacyAccess(id);
        return funcionariosRepository.findById(id)
                .map(FuncionarioResponseDTO::converter)
                .orElseThrow(() -> new NotFoundException("Funcionário não encontrado"));
    }

    public void delete(Long id) {
        checkPrivacyAccess(id);
        if (!funcionariosRepository.existsById(id)) {
            throw new NotFoundException("Funcionário não encontrado");
        }
        funcionariosRepository.deleteById(id);
    }

    public FuncionarioResponseDTO update(FuncionarioUpdateDTO dto) {
        checkPrivacyAccess(dto.getId());
        var funcionario = funcionariosRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Funcionário não encontrado"));

        funcionario.setNome(dto.getNome());
        funcionario.setCargo(dto.getCargo());
        funcionario.setLogin(dto.getLogin());

        if (dto.getNotificacaoAutomatica() != null) {
            funcionario.setNotificacaoAutomatica(dto.getNotificacaoAutomatica());
        }

        var updatedFuncionario = funcionariosRepository.save(funcionario);
        return FuncionarioResponseDTO.converter(updatedFuncionario);
    }

    @Transactional
    public void changePassword(Long id, ChangePasswordDTO dto) {
        checkPrivacyAccess(id);
        var funcionario = funcionariosRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Funcionário não encontrado"));

        // Valida senha atual
        if (!passwordEncoder.matches(dto.getSenhaAtual(), funcionario.getSenha())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha atual incorreta");
        }

        // Atualiza para nova senha
        funcionario.setSenha(passwordEncoder.encode(dto.getNovaSenha()));
        funcionariosRepository.save(funcionario);

        log.info("Senha alterada com sucesso para funcionário ID: {}", id);
    }

    @Transactional
    public AuthResponseDTO authenticate(String login, String senha) {
        var funcionario = funcionariosRepository.findByLogin(login);
        if (funcionario == null) {
            return null;
        }

        boolean matches = passwordEncoder.matches(senha, funcionario.getSenha());

        if (matches) {
            AccessToken accessToken = jwt.getAccessToken(funcionario);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(funcionario);

            return AuthResponseDTO.of(
                    accessToken.getAccessToken(),
                    refreshToken.getToken(),
                    jwt.getExpirationInSeconds());
        }

        return null;
    }

    /**
     * Renova o access token usando um refresh token válido.
     */
    @Transactional
    public AuthResponseDTO refreshAccessToken(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(refreshTokenStr);
        Funcionarios funcionario = refreshToken.getFuncionario();

        // Rotaciona o refresh token (gera um novo)
        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(refreshToken);

        // Gera novo access token
        AccessToken accessToken = jwt.getAccessToken(funcionario);

        return AuthResponseDTO.of(
                accessToken.getAccessToken(),
                newRefreshToken.getToken(),
                jwt.getExpirationInSeconds());
    }

    /**
     * Revoga o refresh token (logout).
     */
    public void logout(String refreshToken) {
        refreshTokenService.revokeToken(refreshToken);
    }

    public Funcionarios findUserByLogin(String login) {
        return funcionariosRepository.findByLogin(login);
    }

    @Transactional
    public Funcionarios saveFuncionarios(FuncionarioCreateDTO funcionarioCreateDTO) {
        var possibleFuncionario = findUserByLogin(funcionarioCreateDTO.getLogin());

        if (possibleFuncionario != null) {
            throw new DuplicatedTupleException("Funcionario já criado");
        }

        encodePassword(funcionarioCreateDTO);
        Funcionarios funcionario = new Funcionarios();
        funcionario.setNome(funcionarioCreateDTO.getNome());
        funcionario.setCargo(funcionarioCreateDTO.getCargo());
        funcionario.setLogin(funcionarioCreateDTO.getLogin());
        funcionario.setSenha(funcionarioCreateDTO.getSenha());

        return funcionariosRepository.save(funcionario);
    }

    private void encodePassword(FuncionarioCreateDTO funcionarioCreateDTO) {
        String hashedPassword = passwordEncoder.encode(funcionarioCreateDTO.getSenha());
        funcionarioCreateDTO.setSenha(hashedPassword);
    }

    private Funcionarios getAuthenticatedFuncionario() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            // Se não estiver autenticado (ex: durante login), retorna null ou lança erro
            // dependendo do caso.
            // Aqui assumimos que métodos protegidos já passaram pelo filtro JWT e têm user.
            // Para findAll/etc que são publicos em tese, mas o filtro só deixa passar se
            // auth.
            return null;
        }
        String login = authentication.getName();
        return funcionariosRepository.findByLogin(login);
    }

    private void checkPrivacyAccess(Long targetId) {
        if (targetId == 1L) {
            Funcionarios authenticatedUser = getAuthenticatedFuncionario();
            if (authenticatedUser == null || authenticatedUser.getId() != 1L) {
                // Se o alvo é ID 1 e quem pede NÃO é ID 1, finge que não existe
                throw new NotFoundException("Funcionário não encontrado");
            }
        }
    }
}
