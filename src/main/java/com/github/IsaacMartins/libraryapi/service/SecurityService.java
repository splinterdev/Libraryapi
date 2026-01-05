package com.github.IsaacMartins.libraryapi.service;

import com.github.IsaacMartins.libraryapi.model.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityService {

    private final UserService userService;

    public UserEntity getLoggedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Retorna um Object que é o UserDetails retornado pelo CustomUserDetailsService (Bean definido no Security Configuration)
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String login = userDetails.getUsername();

        return userService.findByLogin(login);
    }
}
