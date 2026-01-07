package com.github.IsaacMartins.libraryapi.controller.dto.userDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UserDTO(@NotBlank(message = "Required field.")
                      String login,
                      @NotBlank(message = "Required field.")
                      String password,
                      @Email(message = "Invalid email.")
                      @NotBlank(message = "Required field.")
                      String email,
                      List<String> roles) {
}
