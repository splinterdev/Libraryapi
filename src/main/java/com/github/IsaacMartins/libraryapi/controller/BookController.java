package com.github.IsaacMartins.libraryapi.controller;

import com.github.IsaacMartins.libraryapi.controller.dto.bookDTOs.BookWithoutIdDTO;
import com.github.IsaacMartins.libraryapi.controller.dto.bookDTOs.BookWithIdDTO;
import com.github.IsaacMartins.libraryapi.controller.mappers.BookMapper;
import com.github.IsaacMartins.libraryapi.model.entities.Book;
import com.github.IsaacMartins.libraryapi.model.entities.BookGenre;
import com.github.IsaacMartins.libraryapi.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/livros")
@RequiredArgsConstructor
public class BookController implements GenericController {

    private final BookService service;
    private final BookMapper mapper;

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Valid BookWithoutIdDTO bookDTO) {

        Book book = mapper.toEntity(bookDTO);
        service.save(book);
        URI location = generateHeaderLocation(book.getId());
        return ResponseEntity.created(location).build();
    }

    @GetMapping("{id}")
    public ResponseEntity<BookWithIdDTO> searchOne(@PathVariable String id) {

        return service.getBook(UUID.fromString(id)).map(book -> {
            BookWithIdDTO bookDTO = mapper.toBookWithIdDTO(book);
            return ResponseEntity.ok(bookDTO);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {

        return service.getBook(UUID.fromString(id)).map(book -> {
            service.delete(book);
            return ResponseEntity.noContent().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<BookWithIdDTO>> search(@RequestParam(value = "isbn", required = false)
                                                       String isbn,
                                                      @RequestParam(value = "title", required = false)
                                                       String title,
                                                      @RequestParam(value = "author-name", required = false)
                                                       String authorName,
                                                      @RequestParam(value = "genre", required = false)
                                                       BookGenre genre,
                                                      @RequestParam(value = "release-year", required = false)
                                                       Integer releaseYear,
                                                      @RequestParam(value = "page", defaultValue = "0")
                                                       Integer page,
                                                      @RequestParam(value = "page-size", defaultValue = "10")
                                                      Integer pageSize
    ) {

        Page<Book> pageResult = service.searchBySpecification(isbn, title, authorName, genre, releaseYear, page, pageSize);
        // metodo map da interface Page retorna uma nova pagina, sem necessidade de .stream e .toList
        Page<BookWithIdDTO> dtoPage = pageResult.map(mapper::toBookWithIdDTO);

        return ResponseEntity.ok(dtoPage);
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> update(@PathVariable String id, @RequestBody @Valid BookWithoutIdDTO bookDTO) {

        return service.getBook(UUID.fromString(id))
                .map(book -> {

                    Book auxEntity = mapper.toEntity(bookDTO);

                    book.setIsbn(auxEntity.getIsbn());
                    book.setTitle(auxEntity.getTitle());
                    book.setReleaseDate(auxEntity.getReleaseDate());
                    book.setPrice(auxEntity.getPrice());
                    book.setGenre(auxEntity.getGenre());
                    book.setAuthor(auxEntity.getAuthor());

                    service.update(book);

                    return ResponseEntity.noContent().build();

                }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
