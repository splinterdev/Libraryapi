package com.github.IsaacMartins.libraryapi.controller.dto.userDTOs;

import java.util.List;

public record UserDTO(String login, String password, List<String> roles) {
}
