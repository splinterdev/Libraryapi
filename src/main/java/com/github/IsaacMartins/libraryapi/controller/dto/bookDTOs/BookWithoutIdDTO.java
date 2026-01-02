package com.github.IsaacMartins.libraryapi.controller.dto.bookDTOs;

import com.github.IsaacMartins.libraryapi.model.entities.BookGenre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.hibernate.validator.constraints.ISBN;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record BookWithoutIdDTO(@ISBN
                               @NotBlank(message = "Required field!")
                               String isbn,
                               @NotBlank(message = "Required field!")
                               String title,
                               @NotNull(message = "Required field!")
                               @Past(message = "Release date cannot be a future date.")
                               LocalDate releaseDate,
                               BookGenre genre,
                               BigDecimal price,
                               @NotNull(message = "Required field!")
                               UUID authorId) {
}
