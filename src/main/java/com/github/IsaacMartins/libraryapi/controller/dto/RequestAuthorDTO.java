package com.github.IsaacMartins.libraryapi.controller.dto;

import com.github.IsaacMartins.libraryapi.model.entities.Author;

import java.time.LocalDate;

public record RequestAuthorDTO(String name, LocalDate birthDate, String nationality) {

    public Author mapToAuthor() {

        Author author = new Author();

        author.setName(this.name);
        author.setBirthDate(this.birthDate);
        author.setNationality(this.nationality);

        return author;
    }
}
