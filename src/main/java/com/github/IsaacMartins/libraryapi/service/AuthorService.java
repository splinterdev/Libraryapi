package com.github.IsaacMartins.libraryapi.service;

import com.github.IsaacMartins.libraryapi.exceptions.NotAllowedOperation;
import com.github.IsaacMartins.libraryapi.model.entities.Author;
import com.github.IsaacMartins.libraryapi.repository.AuthorRepository;
import com.github.IsaacMartins.libraryapi.repository.BookRepository;
import com.github.IsaacMartins.libraryapi.validator.AuthorValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorValidator validator;
    private final BookRepository bookRepository;

    public AuthorService(AuthorRepository authorRepository, AuthorValidator validator, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.validator = validator;
        this.bookRepository = bookRepository;
    }

    public Author save(Author author) {
        validator.validate(author);
        return authorRepository.save(author);
    }

    public void update(Author author) {
        if(author.getId() == null) {
            throw new IllegalArgumentException("Transient entity can't be updated.");
        }
        validator.validate(author);
        authorRepository.save(author);
    }

    public Optional<Author> getAuthor(UUID id) {
        return authorRepository.findById(id);
    }

    public void delete(Author author) {
        if(hasBooks(author)) {
            throw new NotAllowedOperation("The author cannot be deleted because he has registered books!");
        }
        authorRepository.delete(author);
    }

    public List<Author> search(String name, String nationality) {

        if(name != null && nationality != null) {
            return authorRepository.findByNameAndNationality(name, nationality);
        } else if(name != null) {
            return authorRepository.findByName(name);
        } else if(nationality != null) {
            return authorRepository.findByNationality(nationality);
        }

        return authorRepository.findAll();
    }

    private boolean hasBooks(Author author) {
        return bookRepository.existsByAuthor(author);
    }
}
