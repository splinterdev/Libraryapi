package com.github.IsaacMartins.libraryapi.service;

import com.github.IsaacMartins.libraryapi.model.entities.Book;
import com.github.IsaacMartins.libraryapi.model.entities.BookGenre;
import com.github.IsaacMartins.libraryapi.repository.BookRepository;
import com.github.IsaacMartins.libraryapi.validator.BookValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static com.github.IsaacMartins.libraryapi.repository.specs.BookSpecs.*;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository repository;
    private final BookValidator validator;

    public Book save(Book book) {
        validator.validate(book);
        return repository.save(book);
    }

    public Optional<Book> getBook(UUID id) {
        return repository.findById(id);
    }

    public void delete(Book book) {
        repository.delete(book);
    }

    public Page<Book> searchBySpecification(String isbn,
                                            String title,
                                            String authorName,
                                            BookGenre genre,
                                            Integer releaseYear,
                                            Integer page,
                                            Integer pageSize) {

        //Specification<T> -> interface do jpaRepository que permite fazer querys com query params variáveis assim como a interface Example
        // (alternativa a interface example). Docs: https://jakarta.ee/learn/docs/jakartaee-tutorial/current/persist/persistence-criteria/persistence-criteria.html

        // select * from book where 0 = 0 (Inicializa a specification com uma query verdadeira utilizando conjunction)
        Specification<Book> specs = Specification.where((root, query, cb) -> cb.conjunction());

        if(isbn != null) {
            // usa os metodos static de BookSpecs; specs recebe ela mesma com chamada do .and porque é um objeto imutavel, necessitando receber um novo objeto.
            specs = specs.and(isbnEqual(isbn));
        }

        if(title != null) {
            specs = specs.and(titleLike(title));
        }

        if(authorName != null) {
            specs = specs.and(authorNameLike(authorName));
        }

        if(genre != null) {
            specs = specs.and(genreEqual(genre));
        }

        if(releaseYear != null) {
            specs = specs.and(releaseYearEqual(releaseYear));
        }

        //interface para construir busca paginada
        Pageable pageRequest = PageRequest.of(page, pageSize);

        return repository.findAll(specs, pageRequest);
    }

    public void update(Book book) {
        if(book.getId() == null) {
            throw new IllegalArgumentException("Transient entity can't be updated.");
        }
        validator.validate(book);
        repository.save(book);
    }
}
