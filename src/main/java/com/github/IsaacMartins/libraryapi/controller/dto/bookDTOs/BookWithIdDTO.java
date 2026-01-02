package com.github.IsaacMartins.libraryapi.controller.dto.bookDTOs;

import com.github.IsaacMartins.libraryapi.controller.dto.authorDTOs.AuthorWithoutIdDTO;
import com.github.IsaacMartins.libraryapi.model.entities.BookGenre;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record BookWithIdDTO(UUID id,
                            String isbn,
                            String title,
                            LocalDate releaseDate,
                            BookGenre genre,
                            BigDecimal price,
                            AuthorWithoutIdDTO author) {
}
