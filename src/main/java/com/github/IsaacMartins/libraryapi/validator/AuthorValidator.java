package com.github.IsaacMartins.libraryapi.validator;

import com.github.IsaacMartins.libraryapi.exceptions.DuplicatedRegisterException;
import com.github.IsaacMartins.libraryapi.model.entities.Author;
import com.github.IsaacMartins.libraryapi.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthorValidator {

    private final AuthorRepository repository;

    public void validate(Author author) {
        if(isRegisteredAuthor(author)) {
            throw new DuplicatedRegisterException("Author already registered!");
        }
    }

    private boolean isRegisteredAuthor(Author author) {
        Optional<Author> possibleAuthor = repository.findByNameAndBirthDateAndNationality(author.getName(), author.getBirthDate(), author.getNationality());

        // verifica se a validação está sendo chamada em uma situação de primeiro cadastro (id ainda é nulo pois só existe quando os dados são persistidos pela primeira vez)
        // retorna true se já existe um cadastro com as mesmas informações (isPresent()), caso contrário retorna false.
        if(author.getId() == null) {
            return possibleAuthor.isPresent();
        }

        // verifica se a validação está sendo chamada em uma situação de atualização de um cadastro existente. Caso o id do parametro seja igual ao id do registro no
        // banco de dados, a ação é considerada uma atualização de um dado existente e não lança a DuplicatedRegisterException, permitindo que os dados sejam atualizados.
        return possibleAuthor.isPresent() && !author.getId().equals(possibleAuthor.get().getId());
    }
}
