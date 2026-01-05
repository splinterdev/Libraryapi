package com.github.IsaacMartins.libraryapi.controller;

import com.github.IsaacMartins.libraryapi.controller.dto.userDTOs.UserDTO;
import com.github.IsaacMartins.libraryapi.controller.mappers.UserMapper;
import com.github.IsaacMartins.libraryapi.model.entities.UserEntity;
import com.github.IsaacMartins.libraryapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UserController implements GenericController {

    private final UserService service;
    private final UserMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody UserDTO userDTO) {
        UserEntity user = mapper.toEntity(userDTO);
        service.save(user);
    }
}
