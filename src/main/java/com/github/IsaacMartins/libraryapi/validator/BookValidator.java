package com.github.IsaacMartins.libraryapi.validator;

import com.github.IsaacMartins.libraryapi.exceptions.DuplicatedRegisterException;
import com.github.IsaacMartins.libraryapi.exceptions.RuleException;
import com.github.IsaacMartins.libraryapi.model.entities.Book;
import com.github.IsaacMartins.libraryapi.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BookValidator {

    private static final int YEAR_FOR_PRICE_REQUIREMENT = 2020;

    private final BookRepository repository;

    public void validate(Book book) {
        if(isbnExists(book)) {
            throw new DuplicatedRegisterException("ISBN already exists.");
        }
        if(isRequiredPriceNull(book)) {
            throw new RuleException("price", "Price field required for books released from 2020 onwards.");
        }
    }

    private boolean isbnExists(Book book) {
        Optional<Book> possibleBook = repository.findByIsbn(book.getIsbn());

        if(book.getId() == null) {
            return possibleBook.isPresent();
        }

        //verifica se o livro encontrado tem o mesmo id do livro do parametro; Caso tenha, é o livro a ser atualizado, então o return é false.
        //Caso não tenham o mesmo id, significa que o usuário está tentando atualizar um livro com um ISBN de outro livro no banco de dados, logo o return será true,
        //causando a exception.
        return possibleBook.map(Book::getId).stream().anyMatch(id -> !id.equals(book.getId()));
    }

    private boolean isRequiredPriceNull(Book book) {
        return book.getPrice() == null && book.getReleaseDate().getYear() >= YEAR_FOR_PRICE_REQUIREMENT;
    }
}
