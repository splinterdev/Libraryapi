package com.github.IsaacMartins.libraryapi.controller.dto;

import com.github.IsaacMartins.libraryapi.model.entities.Author;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RequestAuthorDTO(@NotBlank(message = "Required field!")
                               @Size(min = 3, max = 100, message = "Invalid size.")
                               String name,
                               @NotNull(message = "Required field!")
                               @Past(message = "Birh date cannot be a future date.")
                               LocalDate birthDate,
                               @NotBlank(message = "Required field!")
                               @Size(min = 3, max = 50, message = "Invalid size.")
                               String nationality) {

    public Author mapToAuthor() {

        Author author = new Author();

        author.setName(this.name);
        author.setBirthDate(this.birthDate);
        author.setNationality(this.nationality);

        return author;
    }
}
