package com.github.IsaacMartins.libraryapi.security;

import com.github.IsaacMartins.libraryapi.model.entities.UserEntity;
import com.github.IsaacMartins.libraryapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService service;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {

        UserEntity user = service.findByLogin(login);

        if(user == null) {
            throw new UsernameNotFoundException("User not found.");
        }

        return User.builder()
                .username(user.getLogin())
                .password(user.getPassword())
                .roles(user.getRoles().toArray(new String[user.getRoles().size()]))
                .build();
    }
}
