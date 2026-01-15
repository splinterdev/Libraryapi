package com.github.IsaacMartins.libraryapi.repository;

import com.github.IsaacMartins.libraryapi.model.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {


    Client findByClientId(String clientId);
}
