package com.github.IsaacMartins.libraryapi.security;

import com.github.IsaacMartins.libraryapi.model.entities.UserEntity;
import com.github.IsaacMartins.libraryapi.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtCustomAuthenticationFilter extends OncePerRequestFilter {

    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Se ele já for uma instancia de customAuthentication (unico authentication possível além de JwtAuthenticationToken)
        // apenas continua com o filtro de autenticação, sem instanciar um CustomAuthentication
        if(mustConvert(auth)) {

            String login = auth.getName();
            UserEntity findedUser = userService.findByLogin(login);

            if(findedUser != null) {
                auth = new CustomAuthentication(findedUser);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }

    // Verifica se a authentication é uma instancia de JwtAuthenticationToken;
    // Caso seja, realiza o processo de criação de authentication customizada;
    // Necessária verificação pois pode já ser uma instancia de CustomAuthentication,
    // por meio de httpBasic, form login ou Login social externo (nessa aplicação: Google)
    private boolean mustConvert(Authentication auth) {
        return auth instanceof JwtAuthenticationToken;
    }
}
