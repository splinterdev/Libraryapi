package com.github.IsaacMartins.libraryapi.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller // notation que espera retorno de nome de página a partir de uma requisição
public class LoginViewController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/")
    @ResponseBody // coloca o return no corpo da resposta da requisição, para conseguir retornar a mensagem ao envés do nome da página esperada pelo @Controller
    public String homePage(Authentication auth) {
        return "Olá " + auth.getName() + ", Seja bem vindo!";
    }
}
