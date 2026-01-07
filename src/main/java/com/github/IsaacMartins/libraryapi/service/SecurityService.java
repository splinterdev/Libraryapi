package com.github.IsaacMartins.libraryapi.service;

import com.github.IsaacMartins.libraryapi.model.entities.UserEntity;
import com.github.IsaacMartins.libraryapi.security.CustomAuthentication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityService {

    public UserEntity getLoggedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth instanceof CustomAuthentication customAuth) {
            return customAuth.getUser();

        } else {
            return null;
        }
    }
}
