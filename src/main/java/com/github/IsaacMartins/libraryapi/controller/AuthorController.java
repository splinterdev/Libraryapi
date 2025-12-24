package com.github.IsaacMartins.libraryapi.controller;

import com.github.IsaacMartins.libraryapi.controller.dto.ErrorResponse;
import com.github.IsaacMartins.libraryapi.controller.dto.RequestAuthorDTO;
import com.github.IsaacMartins.libraryapi.controller.dto.ResponseAuthorDTO;
import com.github.IsaacMartins.libraryapi.exceptions.DuplicatedRegisterException;
import com.github.IsaacMartins.libraryapi.exceptions.NotAllowedOperation;
import com.github.IsaacMartins.libraryapi.model.entities.Author;
import com.github.IsaacMartins.libraryapi.service.AuthorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/autores")
public class AuthorController {

    private final AuthorService service;

    public AuthorController(AuthorService service) {
        this.service = service;
    }

    /**
     * ResponseEntity<Object> ⇾ Classe para definir o response da request. Define-se o tipo do body da response dentro dos sinais <>
     * com uma WrapperClass do tipo do retorno. Object pois pode não ser retornado nenhum body ou pode ser retornado um DTO de erro (também poderia ser <?>)
     */
    @PostMapping
    public ResponseEntity<Object> save(@RequestBody RequestAuthorDTO requestDTO) {

        try{
            Author authorEntity = requestDTO.mapToAuthor();
            service.save(authorEntity);

            // Isso gera um URL para acessar o autor. Exemplo ⇾ http://localhost:8080/autores/326cff2d-300b-4967-a171-47b81ecc7be7
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(authorEntity.getId())
                    .toUri();

            return ResponseEntity.created(location).build(); //Código: 201 Created; Header: Location - http://localhost:8080/autores/...

        } catch (DuplicatedRegisterException e) {

            ErrorResponse errorDTO = ErrorResponse.conflict(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO); //ResponseEntity por padrão não tem o metodo conflict

        }
    }

    @GetMapping("{id}")
    public ResponseEntity<ResponseAuthorDTO> searchOne(@PathVariable String id) {

        Optional<Author> possibleAuthor = service.getAuthor(UUID.fromString(id));

        if(possibleAuthor.isPresent()) {
            Author author = possibleAuthor.get();
            ResponseAuthorDTO responseDTO = new ResponseAuthorDTO(author.getId(), author.getName(), author.getBirthDate(), author.getNationality());
            return ResponseEntity.ok(responseDTO);
        }

        return ResponseEntity.notFound().build(); // Código: 404 Not Found;
    }

    // poderia retornar apenas 204 No Content mesmo com o autor não encontrado que também estaria certo (conceito de indempotencia)
    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {

        try {
            Optional<Author> possibleAuthor = service.getAuthor(UUID.fromString(id));

            if(possibleAuthor.isEmpty()) {
                return ResponseEntity.notFound().build(); // Código: 404 Not Found;
            }

            service.delete(possibleAuthor.get());

            return ResponseEntity.noContent().build(); // Código: 204 No Content;

        } catch (NotAllowedOperation e) {
            ErrorResponse errorDTO = ErrorResponse.defaultResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
    }

    /**
     * Query params (/autores?name=xxxxx&nationality=yyyyy) com parametros opcionais (required = false). Quando passados os parametros, pesquisa de acordo com eles.
     * Quando não passados os paramtros, faz uma pesquisa de todos sem filtrar por nome e/ou nacionalidade.
     */
    @GetMapping
    public ResponseEntity<List<ResponseAuthorDTO>> search(@RequestParam(value = "name", required = false) String name,
                                                         @RequestParam(value = "nationality", required = false) String nationality) {

        List<ResponseAuthorDTO> authorDTOS = new ArrayList<>();
        List<Author> authors = service.search(name, nationality);

        authors.forEach(a -> authorDTOS.add(new ResponseAuthorDTO(a.getId(), a.getName(), a.getBirthDate(), a.getNationality())));

        return ResponseEntity.ok(authorDTOS); // 200 - OK. Body será um Array de authors
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> update(@PathVariable("id") String id, @RequestBody RequestAuthorDTO authorDTO) {

        try {
            Optional<Author> possibleAuthor = service.getAuthor(UUID.fromString(id));

            if(possibleAuthor.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Author author = possibleAuthor.get();

            author.setName(authorDTO.name());
            author.setNationality(authorDTO.nationality());
            author.setBirthDate(authorDTO.birthDate());

            service.update(author);

            return ResponseEntity.noContent().build();

        } catch (DuplicatedRegisterException e) {

            ErrorResponse errorDTO = ErrorResponse.conflict(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
    }
}
