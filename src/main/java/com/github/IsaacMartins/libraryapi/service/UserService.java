package com.github.IsaacMartins.libraryapi.service;

import com.github.IsaacMartins.libraryapi.model.entities.UserEntity;
import com.github.IsaacMartins.libraryapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public void save(UserEntity user) {
        var password = user.getPassword();
        user.setPassword(encoder.encode(password));
        repository.save(user);
    }

    public UserEntity findByLogin(String login) {
        return repository.findByLogin(login);
    }

    public UserEntity findByEmail(String email) {
        return repository.findByEmail(email);
    }
}
