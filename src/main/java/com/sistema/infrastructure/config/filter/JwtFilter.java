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
        String authorization = getToken(request);
        if (authorization != null) {
            try {
                String login = jwt.getLoginFromToken(authorization);
                Funcionarios funcionarios = funcionariosServices.findUserByLogin(login);
                setUserAsAuthenticated(funcionarios);
            } catch (InvalidTokenException e) {
                log.error("Token inválido: {}",e.getMessage());
                throw new InvalidTokenException(e.getMessage());
            } catch (Exception e){
                log.error("Erro na validação do token: {}",e.getMessage());
                throw new InvalidTokenException(e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
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
