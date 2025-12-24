package com.github.IsaacMartins.libraryapi.repository;

import com.github.IsaacMartins.libraryapi.model.entities.Author;
import com.github.IsaacMartins.libraryapi.model.entities.Book;
import com.github.IsaacMartins.libraryapi.model.entities.BookGenre;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@SpringBootTest
public class AuthorRepositoryTest {

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    BookRepository bookRepository;

    @Test
    void createTest() {
        Book book = new Book();
        book.setIsbn("90887-84874");
        book.setPrice(BigDecimal.valueOf(100));
        book.setGenre(BookGenre.CIENCIA);
        book.setTitle("Ciencias");
        book.setReleaseDate(LocalDate.of(1980, 1, 2));

        Author author = authorRepository.findById(UUID.fromString("7aa64612-8af7-4db8-97c8-872a64a97211")).orElse(null);

        book.setAuthor(author);

        bookRepository.save(book);
    }
}
