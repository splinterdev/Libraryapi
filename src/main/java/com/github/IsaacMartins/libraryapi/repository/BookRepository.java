package com.github.IsaacMartins.libraryapi.repository;

import com.github.IsaacMartins.libraryapi.model.entities.Author;
import com.github.IsaacMartins.libraryapi.model.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {

    boolean existsByAuthor(Author author);
}
