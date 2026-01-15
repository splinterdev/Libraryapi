package com.github.IsaacMartins.libraryapi.service;

import com.github.IsaacMartins.libraryapi.model.entities.Client;
import com.github.IsaacMartins.libraryapi.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository repository;
    private final PasswordEncoder encoder;

    public Client save(Client client) {
        client.setClientSecret(encoder.encode(client.getClientSecret()));
        return repository.save(client);
    }

    public Client findByClientId(String clientId) {
        return repository.findByClientId(clientId);
    }
}
