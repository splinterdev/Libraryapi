package com.github.IsaacMartins.libraryapi.controller.dto.authorDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AuthorWithoutIdDTO(@NotBlank(message = "Required field!")
                                 @Size(min = 3, max = 100, message = "Invalid size.")
                                 String name,
                                 @NotNull(message = "Required field!")
                                 @Past(message = "Birh date cannot be a future date.")
                                 LocalDate birthDate,
                                 @NotBlank(message = "Required field!")
                                 @Size(min = 3, max = 50, message = "Invalid size.")
                                 String nationality) {
}
