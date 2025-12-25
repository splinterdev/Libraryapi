package com.github.IsaacMartins.libraryapi.service;

import com.github.IsaacMartins.libraryapi.exceptions.NotAllowedOperation;
import com.github.IsaacMartins.libraryapi.model.entities.Author;
import com.github.IsaacMartins.libraryapi.repository.AuthorRepository;
import com.github.IsaacMartins.libraryapi.repository.BookRepository;
import com.github.IsaacMartins.libraryapi.validator.AuthorValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorValidator validator;
    private final BookRepository bookRepository;

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
            throw new NotAllowedOperation("The author cannot be deleted because he has registered books.");
        }
        authorRepository.delete(author);
    }

    public List<Author> searchByExample(String name, String nationality) {
        Author author = new Author();
        author.setName(name);
        author.setNationality(nationality);
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreNullValues()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING); // metodo que faz a query com like. No caso de containing é o mesmo que: like %string%;
        Example<Author> authorExample = Example.of(author, matcher);
        return authorRepository.findAll(authorExample);
    }

    private boolean hasBooks(Author author) {
        return bookRepository.existsByAuthor(author);
    }
}
