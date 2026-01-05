package com.github.IsaacMartins.libraryapi.controller;

import com.github.IsaacMartins.libraryapi.controller.dto.authorDTOs.AuthorWithoutIdDTO;
import com.github.IsaacMartins.libraryapi.controller.dto.authorDTOs.AuthorWithIdDTO;
import com.github.IsaacMartins.libraryapi.controller.mappers.AuthorMapper;
import com.github.IsaacMartins.libraryapi.model.entities.Author;
import com.github.IsaacMartins.libraryapi.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/autores")
@RequiredArgsConstructor
public class AuthorController implements GenericController {

    private final AuthorService authorService;

    private final AuthorMapper mapper;

    /**
     * ResponseEntity<Object> ⇾ Classe para definir o response da request. Define-se o tipo do body da response dentro dos sinais <>
     * com uma WrapperClass do tipo do retorno. Object pois pode não ser retornado nenhum body ou pode ser retornado um DTO de erro (também poderia ser <?>)
     */
    @PostMapping
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Void> save(@RequestBody @Valid AuthorWithoutIdDTO requestDTO) {

        Author author = mapper.toEntity(requestDTO);
        authorService.save(author);

        // Isso gera um URL para acessar o autor. Exemplo ⇾ http://localhost:8080/autores/326cff2d-300b-4967-a171-47b81ecc7be7
        URI location = generateHeaderLocation(author.getId());

        return ResponseEntity.created(location).build(); //Código: 201 Created; Header: Location - http://localhost:8080/autores/...
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    public ResponseEntity<AuthorWithIdDTO> searchOne(@PathVariable String id) {

        return authorService.getAuthor(UUID.fromString(id))
                .map(author -> {
                    AuthorWithIdDTO responseDTO = mapper.toAuthorWithIdDTO(author);
                    return ResponseEntity.ok(responseDTO);
                }).orElseGet(() -> ResponseEntity.notFound().build()); // Código: 404 Not Found. Se o optional de getAuthor não estiver presente, o retorno será o orElseGet();
    }

    // poderia retornar apenas 204 No Content mesmo com o autor não encontrado que também estaria certo (conceito de indempotencia)
    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Void> delete(@PathVariable String id) {

        Optional<Author> possibleAuthor = authorService.getAuthor(UUID.fromString(id));

        if(possibleAuthor.isEmpty()) {
            return ResponseEntity.notFound().build(); // Código: 404 Not Found;
        }

        authorService.delete(possibleAuthor.get());

        return ResponseEntity.noContent().build(); // Código: 204 No Content;
    }

    /**
     * Query params (/autores?name=xxxxx&nationality=yyyyy) com parametros opcionais (required = false). Quando passados os parametros, pesquisa de acordo com eles.
     * Quando não passados os paramtros, faz uma pesquisa de todos sem filtrar por nome e/ou nacionalidade.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    public ResponseEntity<List<AuthorWithIdDTO>> search(@RequestParam(value = "name", required = false) String name,
                                                        @RequestParam(value = "nationality", required = false) String nationality) {

        List<Author> authors = authorService.searchByExample(name, nationality);
        List<AuthorWithIdDTO> authorDTOS = authors
                        .stream()
                        .map(mapper::toAuthorWithIdDTO)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(authorDTOS); // 200 - OK. Body será um Array de authors
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Void> update(@PathVariable("id") String id, @RequestBody @Valid AuthorWithoutIdDTO authorDTO) {

        Optional<Author> possibleAuthor = authorService.getAuthor(UUID.fromString(id));

        if(possibleAuthor.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Author author = possibleAuthor.get();

        author.setName(authorDTO.name());
        author.setNationality(authorDTO.nationality());
        author.setBirthDate(authorDTO.birthDate());

        authorService.update(author);

        return ResponseEntity.noContent().build();
    }
}
