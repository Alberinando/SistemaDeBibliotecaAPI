package com.sistema.infrastructure.config.filter;

import com.sistema.domain.entities.Funcionarios;
import com.sistema.domain.services.FuncionariosServices;
import com.sistema.infrastructure.exceptions.InvalidTokenException;
import com.sistema.infrastructure.security.Jwt.Jwt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final Jwt jwt;
    private final FuncionariosServices funcionariosServices;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        
        if (path.equals("/v1/funcionario/auth") || path.equals("/") || path.equals("/index.html") || 
            path.startsWith("/actuator/")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String authorization = getToken(request);

        if (authorization == null) {
            log.debug("JwtFilter: nenhum Authorization header encontrado para {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String login = jwt.getLoginFromToken(authorization);
            if (login == null) {
                log.warn("JwtFilter: token sem login");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
                return;
            }

            Funcionarios funcionarios = funcionariosServices.findUserByLogin(login);
            if (funcionarios == null) {
                log.warn("JwtFilter: usuário não encontrado para login do token: {}", login);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuário do token não encontrado");
                return;
            }

            setUserAsAuthenticated(funcionarios);
            filterChain.doFilter(request, response);
        } catch (InvalidTokenException e) {
            log.warn("JwtFilter: token inválido - {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
        } catch (Exception e) {
            log.error("JwtFilter: erro ao validar token", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Erro ao validar token");
        }
    }

    private void setUserAsAuthenticated(Funcionarios funcionarios) {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(funcionarios.getLogin())
                .password(funcionarios.getSenha())
                .roles("USER")
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null){
            String[] authHeaderParts = authHeader.split(" ");
            if (authHeaderParts.length == 2){
                return authHeaderParts[1];
            }
        }
        return null;
    }
}
