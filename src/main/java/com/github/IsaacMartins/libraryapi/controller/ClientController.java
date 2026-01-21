package com.github.IsaacMartins.libraryapi.controller;

import com.github.IsaacMartins.libraryapi.model.entities.Client;
import com.github.IsaacMartins.libraryapi.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
@Slf4j
public class ClientController {

    private final ClientService service;

    // controller sem DTOs e sem validações de DTOs ou entidades no ClientService pois o foco é realizar um authorization server.
    // Teria toda a divisão de camadas e verificação em um projeto normal
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('GERENTE')")
    public void save(@RequestBody Client client) {
        log.info("Registrando novo client: {} com scope: {}", client.getClientId(), client.getScope());
        service.save(client);
    }
}
